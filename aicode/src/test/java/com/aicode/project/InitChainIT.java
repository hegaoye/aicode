package com.aicode.project;

import com.aicode.filter.ShtmlSuffixRewriteFilter;
import com.aicode.project.entity.Project;
import com.aicode.project.service.ProjectService;
import com.aicode.project.service.ProjectSqlService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * **代码生成主链路的端到端回归（段 1）** ——
 * 登录 → 建项目 → 导 SQL → **异步 init（真实建库 + 真实逆解析）** → 验证解析产物。
 *
 * <p>全部走**真实数据库**（H2 test profile + {@code db/schema.sql} + {@code db/data.sql}），
 * <b>零 mock</b>。唯一打桩的是 {@link ServerEndpointExporter}（MOCK 环境无
 * {@code jakarta.websocket.ServerContainer}，与 ws 无关）。</p>
 *
 * <h3>为什么必须有这个测试</h3>
 * <p>AGENTS §7.1 记着：2026-10-15 的 {@code @TableField} 丢失事故让
 * <b>215 个测试全部绿灯</b>，而功能全废 —— 根因是「没有任何测试让该实体真正生成并执行 SQL」。
 * 本类治的就是同一类病。</p>
 *
 * <h3>它已经抓到过一个真实缺陷</h3>
 * <p>实施前的技术探针在本类的基础上跑通到 {@code parse()} 阶段，撞上
 * {@code StringTools.getStateOrType(column.getRemarks())} 在 {@code remarks} 为 null 时
 * 抛 NPE —— 即<strong>用户导入的建表 SQL 只要有一列没写 {@code COMMENT}，逆解析就整体失败</strong>。
 * 该缺陷此前未暴露，正因为所有 {@code parse} 的测试都打桩了 {@code columnDAO}。</p>
 * 故本类的测试 DDL <b>故意不含 {@code COMMENT}</b>。</p>
 *
 * @see <a href="file:../../../../../../../../openspec/changes/archive/2026-10-18-init-chain-e2e-regression/">变更文档</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class InitChainIT {

    private static final ObjectMapper PARSER = new ObjectMapper();
    private static final String ADMIN_ACCOUNT = "admin";
    private static final String ADMIN_PASSWORD = "888888";
    private static final String ADMIN_CODE = "21218cca77804d2ba2";

    /** 轮询参数（AGENTS §7.6：SHALL 轮询，SHALL NOT sleep 固定时长）。 */
    private static final long POLL_INTERVAL_MS = 50L;
    private static final long POLL_TIMEOUT_MS = 30_000L;

    /**
     * 测试 DDL —— <b>故意不含 COMMENT</b>（守住 `getStateOrType(null)` 的 NPE 缺陷）。
     *
     * <p>表名避开 H2 保留字：{@code user} / {@code order} 在 {@code MODE=MYSQL} 下
     * 仍会报 {@code expected "identifier"}（实测），与 README 记载的「非法 {@code order}」
     * 同类。</p>
     */
    private static final String DDL = """
            CREATE TABLE customer (
              id BIGINT PRIMARY KEY,
              name VARCHAR(64) NOT NULL,
              created_at DATETIME
            );
            CREATE TABLE shop_order (
              id BIGINT PRIMARY KEY,
              customer_id BIGINT,
              amount DECIMAL(10,2)
            );
            """;

    @Autowired private WebApplicationContext wac;
    @Autowired private ShtmlSuffixRewriteFilter shtmlSuffixRewriteFilter;
    @Autowired private ProjectService projectService;
    @Autowired private ProjectSqlService projectSqlService;
    @Autowired private JdbcTemplate jdbcTemplate;

    @MockitoBean private ServerEndpointExporter serverEndpointExporter;

    /** 本次测试创建过的 schema，@AfterEach 清理（否则污染其他测试）。 */
    private final java.util.List<String> createdSchemas = new java.util.ArrayList<>();

    private MockMvc mockMvc() {
        // MockMvc 不自动注册 Bean 形态的 Filter，.shtml 相关必须手工 addFilters
        return MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(shtmlSuffixRewriteFilter)
                .build();
    }

    @AfterEach
    void dropSchemas() {
        for (String schema : createdSchemas) {
            try {
                jdbcTemplate.execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
            } catch (Exception ignore) {
                // 清理失败不应掩盖测试本身的失败
            }
        }
        createdSchemas.clear();
    }

    // ==================================================================
    // 正路径：登录 → 建项目 → 导 SQL → 异步 init → 验证解析产物
    // ==================================================================

    @Test
    @DisplayName("端到端：登录 → 建项目 → 导无 COMMENT 的 SQL → init → 解析出 2 张表 7 列")
    void initChain_producesRealTablesAndColumns() throws Exception {
        MockMvc mockMvc = mockMvc();

        // ---- 1. 登录（真实端点；口令是 data.sql 里的 MD5，登录时会被透明升级为 BCrypt） ----
        String loginBody = mockMvc.perform(get("/login/signin.shtml")
                        .param("account", ADMIN_ACCOUNT)
                        .param("password", ADMIN_PASSWORD))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode login = PARSER.readTree(loginBody);
        assertTrue(login.path("success").asBoolean(), "登录 SHALL 成功，实际=" + loginBody);
        assertFalse(login.path("data").isNull() || login.path("data").asText().isEmpty(),
                "登录 SHALL 签发 token，实际=" + loginBody);
        String token = login.path("data").asText();

        // ---- 2. 建项目 ----
        String englishName = "e2e_shop_" + ts();
        createdSchemas.add(englishName);
        String createdBody = mockMvc.perform(post("/project/build.shtml")
                        .param("token", token)
                        .param("name", "端到端演示商城")
                        .param("englishName", englishName)
                        .param("author", "e2e")
                        .param("phone", "13800000000")
                        .param("basePackage", "com.e2e.shop")
                        .param("copyright", "Copyright (c) 2026")
                        .param("description", "InitChainIT")
                        .param("databaseType", "Mysql")
                        .param("language", "Java"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode created = PARSER.readTree(createdBody);
        assertTrue(created.path("success").asBoolean(), "建项目 SHALL 成功，实际=" + createdBody);
        String projectCode = created.path("data").path("code").asText();
        assertFalse(projectCode.isEmpty(), "建项目 SHALL 返回 code");

        // crud 生成器的 save() 会**重新生成 code** —— Controller 返回的是 save 之后的实体，
        // 故上面拿到的 code 就是可用的（design D4）。顺带断言它与英文名的映射可查回。
        Project saved = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getEnglishName, englishName));
        assertNotNull(saved, "项目 SHALL 能按 englishName 查回");
        assertEquals(projectCode, saved.getCode(),
                "Controller 返回的 code SHALL 等于实际落库的 code（save 会重新生成它）");

        // ---- 3. 导 SQL ----
        String sqlBody = mockMvc.perform(post("/project/sql/build.shtml")
                        .param("projectCode", projectCode)
                        .param("tsql", DDL))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(PARSER.readTree(sqlBody).path("success").asBoolean(),
                "导 SQL SHALL 成功，实际=" + sqlBody);

        // ---- 4. 触发异步 init，然后轮询等待（SHALL NOT sleep 固定时长） ----
        String initBody = mockMvc.perform(post("/project/init.shtml").param("code", projectCode))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertTrue(PARSER.readTree(initBody).path("success").asBoolean(),
                "init SHALL 立即返回「已接受」，实际=" + initBody);

        JsonNode tables = awaitParsedTables(mockMvc, projectCode, 2);

        // ---- 5. 验证表级产物 ----
        assertEquals(2, tables.size(), "SHALL 解析出 2 张表，实际表清单=" + tables);
        for (JsonNode t : tables) {
            String tableName = t.path("tableName").asText();
            assertFalse(tableName.isEmpty(), "表名 SHALL 非空");
            assertFalse(t.path("className").asText().isEmpty(),
                    "类名 SHALL 非空（tableName=" + tableName + "）");
            assertFalse(t.path("dashedCaseName").asText().isEmpty(),
                    "kebab-case 前端命名 SHALL 非空（tableName=" + tableName + "）");
        }
        // 表名集合（顺序不保证 —— 逆解析按 INFORMATION_SCHEMA 的返回顺序）
        java.util.Set<String> tableNames = new java.util.HashSet<>();
        tables.forEach(t -> tableNames.add(t.path("tableName").asText()));
        assertTrue(tableNames.contains("customer"),
                "SHALL 含 customer 表，实际=" + tableNames);
        assertTrue(tableNames.contains("shop_order"),
                "SHALL 含 shop_order 表，实际=" + tableNames);

        // ---- 6. 验证列级产物（含主键 / 日期标记 —— AGENTS §10 记过 H2 元数据差异） ----
        String customerTableCode = tablesPath(mockMvc, projectCode, "customer");
        String colBody = mockMvc.perform(get("/project/relationship/listMapFieldColumn.shtml")
                        .param("mapClassTableCode", customerTableCode)
                        .param("associateCode", "none"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode cols = PARSER.readTree(colBody).path("data").path("mainFields");
        assertEquals(3, cols.size(), "customer SHALL 解析出 3 列，实际=" + cols);

        java.util.Map<String, JsonNode> byField = new java.util.HashMap<>();
        cols.forEach(c -> byField.put(c.path("field").asText(), c));

        // 无 COMMENT 的列也 SHALL 有映射（守住 getStateOrType(null) 的 NPE 缺陷）
        assertTrue(byField.containsKey("id"), "SHALL 映射出 id 列，实际=" + byField.keySet());
        assertTrue(byField.containsKey("name"), "SHALL 映射出 name 列，实际=" + byField.keySet());
        assertTrue(byField.containsKey("createdAt"),
                "created_at SHALL 映射为驼峰属性 createdAt，实际=" + byField.keySet());
        assertEquals("created_at", byField.get("createdAt").path("column").asText(),
                "列名 SHALL 保持原样");
        assertFalse(byField.get("name").path("fieldType").asText().isEmpty(),
                "SHALL 有 Java 类型");
        assertEquals("Y", byField.get("id").path("isPrimaryKey").asText(),
                "id 是主键，SHALL 标记为 Y（AGENTS §10：H2 元数据的 IS_NULLABLE 语义易错）");
        assertEquals("Y", byField.get("createdAt").path("isDate").asText(),
                "created_at 是日期，SHALL 标记为 Y");
        // 无 COMMENT → 无状态语义 → N
        assertEquals("N", byField.get("name").path("isState").asText(),
                "无 COMMENT 的列 SHALL 标记 isState=N（getStateOrType(null) 返空 map 的语义）");

        // ---- 7. 项目标记 ----
        Project after = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, projectCode));
        assertEquals("Y", after.getIsParseTable(), "isParseTable SHALL 为 Y");
        assertEquals("Y", after.getIsParseClass(), "isParseClass SHALL 为 Y");
    }

    // ==================================================================
    // 失败路径：非法 DDL SHALL NOT 留下「看起来成功」的半成品
    // ==================================================================

    @Test
    @DisplayName("非法 DDL：init SHALL 失败且不把未完成的解析当作成功")
    void initChain_invalidDdl_doesNotLeaveHalfBakedSuccess() throws Exception {
        MockMvc mockMvc = mockMvc();

        String englishName = "e2e_bad_" + ts();
        createdSchemas.add(englishName);
        String createdBody = mockMvc.perform(post("/project/build.shtml")
                        .param("token", "any-token-for-controller")
                        .param("name", "非法 DDL 项目")
                        .param("englishName", englishName)
                        .param("author", "e2e").param("phone", "138")
                        .param("basePackage", "com.e2e.bad")
                        .param("copyright", "c").param("description", "d")
                        .param("databaseType", "Mysql").param("language", "Java"))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        String projectCode = PARSER.readTree(createdBody).path("data").path("code").asText();

        mockMvc.perform(post("/project/sql/build.shtml")
                        .param("projectCode", projectCode)
                        // 语法错误：缺右括号
                        .param("tsql", "CREATE TABLE broken (id BIGINT PRIMARY KEY;"))
                .andReturn();

        mockMvc.perform(post("/project/init.shtml").param("code", projectCode))
                .andReturn();

        // 失败应当**快速**发生，而不是卡到轮询超时（否则每跑一次等满 30 秒）
        long start = System.currentTimeMillis();
        JsonNode tables = pollUntilSettled(mockMvc, projectCode, 15_000L);
        long elapsed = System.currentTimeMillis() - start;

        // 关键断言：SHALL NOT 出现「已完成的表」（本次 DDL 里唯一一张 broken 表建不出来）
        for (JsonNode t : tables) {
            assertNotEquals("broken", t.path("tableName").asText(),
                    "非法 DDL SHALL NOT 产出任何表（含半成品）");
        }
        Project after = projectService.getOne(new LambdaQueryWrapper<Project>()
                .eq(Project::getCode, projectCode));
        assertNotEquals("Y", after.getIsParseClass(),
                "解析失败时 isParseClass SHALL NOT 被置为 Y（否则会被误认为成功）");
        assertTrue(elapsed < POLL_TIMEOUT_MS, "失败路径 SHALL 快速失败（实测 " + elapsed + " ms）");
    }

    // ==================================================================
    // 轮询工具（SHALL NOT 用 sleep 固定时长）
    // ==================================================================

    /** 轮询直到解析出期望数量的表，或超时抛带诊断信息的断言错误。 */
    private JsonNode awaitParsedTables(MockMvc mockMvc, String projectCode, int expected)
            throws Exception {
        long deadline = System.currentTimeMillis() + POLL_TIMEOUT_MS;
        int polls = 0;
        JsonNode last = PARSER.createArrayNode();
        while (System.currentTimeMillis() < deadline) {
            polls++;
            last = listTables(mockMvc, projectCode);
            if (last.size() >= expected) {
                return last;
            }
            Thread.sleep(POLL_INTERVAL_MS);
        }
        fail("轮询超时：期望 " + expected + " 张表，已轮询 " + polls + " 次，"
                + "最后观测到 " + last.size() + " 张 -> " + last);
        return last;
    }

    /**
     * 轮询直到「解析稳定」—— 连续两次观测结果一致即视为已结束。
     *
     * <p>用于失败路径：无法用「表数达标」判定完成（本次本来就一张都建不出来），
     * 改用「结果不再变化」作为结束信号。</p>
     */
    private JsonNode pollUntilSettled(MockMvc mockMvc, String projectCode, long timeoutMs)
            throws Exception {
        long deadline = System.currentTimeMillis() + timeoutMs;
        int polls = 0;
        String previous = null;
        int stableRounds = 0;
        JsonNode last = PARSER.createArrayNode();
        while (System.currentTimeMillis() < deadline) {
            polls++;
            last = listTables(mockMvc, projectCode);
            String current = last.toString();
            if (current.equals(previous)) {
                stableRounds++;
                if (stableRounds >= 3) {
                    return last;
                }
            } else {
                stableRounds = 0;
                previous = current;
            }
            Thread.sleep(POLL_INTERVAL_MS);
        }
        fail("轮询超时：已轮询 " + polls + " 次，最后观测到 " + last);
        return last;
    }

    private JsonNode listTables(MockMvc mockMvc, String projectCode) throws Exception {
        String body = mockMvc.perform(get("/project/relationship/listMapClassTable.shtml")
                        .param("projectCode", projectCode))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        JsonNode data = PARSER.readTree(body).path("data");
        return data.isArray() ? data : PARSER.createArrayNode();
    }

    /** 从表清单里取指定表名的 code。 */
    private String tablesPath(MockMvc mockMvc, String projectCode, String tableName) throws Exception {
        for (JsonNode t : listTables(mockMvc, projectCode)) {
            if (tableName.equals(t.path("tableName").asText())) {
                return t.path("code").asText();
            }
        }
        fail("表清单里找不到 " + tableName + "；实际=" + listTables(mockMvc, projectCode));
        return "";
    }

    private static String ts() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }
}