package com.aicode.contract;

import com.aicode.core.enums.Constants;
import com.aicode.core.tools.JwtToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import com.aicode.project.dao.mapper.ProjectMapper;
import com.aicode.project.dao.mapper.ProjectSqlMapper;
import com.aicode.project.entity.Project;
import com.aicode.project.entity.ProjectSql;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 接口契约回归测试 —— Java 25 / Spring Boot 4.1 升级系列的<b>验收金标准</b>。
 *
 * <p>这些断言 SHALL 在升级前后的<b>每一个</b>版本上全部通过。任何一条变红，
 * 都意味着某次升级破坏了前后端之间从未被文档化的隐式契约。</p>
 *
 * <p>为什么必须用真实 {@code WebMvcConfigurer} 而非全量 mock：{@code .shtml} 后缀可达性
 * 是<b>路由层</b>行为，mock 掉就测不到。业务层（Service）返回什么与路由无关，可放心打桩。</p>
 *
 * @see FrontendEndpointCatalog 前端端点清单
 * @see <a href="file:../../../../../../../../../openspec/changes/2026-10-14-add-api-contract-tests/specs/api-contract/spec.md">api-contract 能力</a>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class ApiContractIT {

    /** Jackson：仅用于<b>解析</b>响应做结构断言，不用于序列化（序列化由被测应用负责）。 */
    private static final ObjectMapper PARSER = new ObjectMapper();

    private static final String EXPECTED_DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    private static final DateTimeFormatter EXPECTED_DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern SNOWFLAKE_CODE = Pattern.compile("^\\d{1,19}$");
    private static final Pattern DATE = Pattern.compile(EXPECTED_DATE_PATTERN);

    /** 静态资源里已知的 19 位雪花 ID（见 db/data.sql 中 frameworks.code）。 */
    private static final String KNOWN_SNOWFLAKE_CODE = "1882812765213425664";

    @Autowired
    private WebApplicationContext wac;

    /**
     * Spring Framework 7 移除了 {@code PathMatchConfigurer#setUseSuffixPatternMatch}，
     * {@code .shtml} 兼容改由 {@link com.aicode.filter.ShtmlSuffixRewriteFilter} 提供。
     *
     * <p><b>必须显式注册</b>：{@code MockMvcBuilders.webAppContextSetup(wac)} 只装配
     * {@code DispatcherServlet}，不会把 Spring Bean 形态的 {@link jakarta.servlet.Filter}
     * 加入 MockFilterChain —— 真实运行时由嵌入式容器自动注册，MockMvc 下需手动
     * {@code addFilters(...)}，否则 {@code .shtml} 请求会落到静态资源处理器并报
     * {@code NoResourceFoundException}。</p>
     */
    @Autowired
    private com.aicode.filter.ShtmlSuffixRewriteFilter shtmlSuffixRewriteFilter;

    /**
     * MOCK 环境（{@code WebEnvironment.MOCK}）无嵌入式容器，不存在
     * {@code jakarta.websocket.server.ServerContainer}，导致
     * {@code WebSocketConfig#serverEndpointExporter} 创建失败 —— 应用上下文直接起不来。
     *
     * <p>此处打桩的是 <b>WebSocket 端点注册器</b>，与业务 Service 不同：
     * 它依赖真实容器，而契约断言关注的是 REST 路由与序列化，两者在 MOCK 下本就不可兼得。
     * 场景⑥ 改用「路径未被静态资源处理器吞掉」来间接验证 WebSocket 端点的存在。</p>
     *
     * <p>本文件已随 {@code openspec/changes/2026-10-14-upgrade-spring-boot-3-5}
     * 迁移到 {@code @MockitoBean}（Spring Framework 6.2 引入；Boot 4.0 起
     * {@code @MockBean} 已被移除，迁移是强制项）。</p>
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    /**
     * 业务层 Service 一律<b>不</b>打桩：契约断言需要真实的 MyBatis 查询与 Jackson 序列化，
     * mock 掉之后验证的就只是「序列化行为」而非「真实接口返回」，反而失去意义。
     */
    private MockMvc mockMvc;
    private String token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(shtmlSuffixRewriteFilter)   // 见上方字段注释：MockMvc 不会自动注册 Bean 形态的 Filter
                .build();
        // 走真实鉴权链路：不 mock LoginInterceptor，而是签发一个合法 JWT
        token = JwtToken.createToken(Constants.AccountCode.val.toString(), "contract-test");
    }

    // ==================================================================
    // 场景①：标识符以 JSON 字符串形式序列化
    // ==================================================================

    @Nested
    @DisplayName("场景① 标识符以 JSON 字符串形式序列化")
    class IdentifierSerialization {

        @Test
        @DisplayName("列表接口的 id 是 JSON 字符串，不是数字")
        void id_isJsonString_notNumber() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);

            JsonNode first = firstRecord(body);
            assertThat(first).as("列表至少返回一条记录（data.sql 中 frameworks 有初始数据）").isNotNull();
            assertThat(first.get("id").isTextual())
                    .as("id 的 JSON 类型 SHALL 为字符串，实际节点类型=%s，值=%s",
                            first.get("id").getNodeType(), first.get("id"))
                    .isTrue();
        }

        @Test
        @DisplayName("code 是 JSON 字符串，且匹配 ^\\d{1,19}$")
        void code_isJsonString_matchingSnowflake() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode records = PARSER.readTree(body).path("data").path("records");
            assertThat(records.isArray()).isTrue();
            assertThat(records).isNotEmpty();

            for (JsonNode record : records) {
                JsonNode code = record.get("code");
                assertThat(code.isTextual()).as("code SHALL 为字符串，实际=%s", code).isTrue();
                assertThat(code.asText()).matches(SNOWFLAKE_CODE);
            }
        }

        @Test
        @DisplayName("19 位标识符逐字符不被截断（Jackson 3 迁移的核心防线）")
        void snowflakeId_notTruncated() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode records = PARSER.readTree(body).path("data").path("records");
            assertThat(records).anySatisfy(record ->
                    assertThat(record.get("code").asText())
                            .as("db/data.sql 中存在 19 位雪花编码 %s，SHALL 逐字符返回",
                                    KNOWN_SNOWFLAKE_CODE)
                            .isEqualTo(KNOWN_SNOWFLAKE_CODE));
        }

        @Test
        @DisplayName("若 id 是数字，JSON.parse 后的值会失真 —— 直接断言其字符串形态可防此类回归")
        void id_wouldLosePrecision_ifNumeric() throws Exception {
            // 前端为 Angular 7 + jQuery，无 BigInt，客户端无法补救，故服务端必须返回字符串
            long beyondSafeInteger = 1882812765213425664L;
            assertThat(beyondSafeInteger)
                    .as("该值超出 JS Number.MAX_SAFE_INTEGER(%d)，这是契约存在的根本原因",
                            9007199254740992L)
                    .isGreaterThan(9007199254740992L);

            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);
            JsonNode rawId = PARSER.readTree(body).path("data").path("records").get(0).get("id");
            assertThat(rawId.toString())
                    .as("原始 JSON 文本中 id 必须带引号（当前=%s）", rawId)
                    .startsWith("\"").endsWith("\"");
        }
    }

    // ==================================================================
    // 场景②：.shtml 与无后缀路径等价可达
    // ==================================================================

    @Nested
    @DisplayName("场景② .shtml 与无后缀路径等价可达")
    class ShtmlSuffixCompatibility {

        static Stream<String> reachableEndpoints() {
            return FrontendEndpointCatalog.REACHABLE.stream();
        }

        @ParameterizedTest(name = "[{index}] {0}")
        @MethodSource("reachableEndpoints")
        @DisplayName("带 .shtml 与不带后缀返回一致的状态码")
        void shtml_andPlain_haveSameStatus(String path) throws Exception {
            // GET 探测：多数端点为 POST/GET 混合，这里只比较两者是否都「可达」
            // （均不为 404 即视为路由命中），不比较响应体 —— 端点的请求方法与参数各不相同
            int shtmlStatus = probeStatus(mockMvc.perform(
                    MockMvcRequestBuilders.get(path + ".shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", token)).andReturn());
            int plainStatus = probeStatus(mockMvc.perform(
                    MockMvcRequestBuilders.get(path)
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", token)).andReturn());

            assertThat(shtmlStatus)
                    .as("%s.shtml SHALL 可达（不得 404）", path)
                    .isNotEqualTo(404);
            assertThat(shtmlStatus)
                    .as("%s.shtml 与 %s 的状态码 SHALL 一致", path, path)
                    .isEqualTo(plainStatus);
        }

        private int probeStatus(MvcResult result) {
            return result.getResponse().getStatus();
        }
    }

    // ==================================================================
    // 场景③：统一响应结构 R
    // ==================================================================

    @Nested
    @DisplayName("场景③ 统一响应结构")
    class UnifiedResponse {

        @Test
        @DisplayName("成功响应满足 code=0000、success=true、含 data 键")
        void successShape() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);
            JsonNode root = PARSER.readTree(body);

            assertThat(root.get("code").asText()).isEqualTo("0000");
            assertThat(root.get("success").asBoolean()).isTrue();
            assertThat(root.has("data")).as("顶层 SHALL 始终含 data 键").isTrue();
            assertThat(root.has("info")).as("顶层 SHALL 含 info 键").isTrue();
        }

        /**
         * 【已知缺陷 — 已确认不修复，本断言长期保持禁用】
         *
         * <p>当前实现下本断言<b>失败</b>：空 token 请求 {@code /framework/list.shtml}
         * 返回 200 + 完整业务数据，而非 9007。</p>
         *
         * <p><b>根因</b>：{@code ContextConfiguration#addInterceptors} 的
         * {@code excludePathPatterns} 中有一条「双星 + 斜杠 + 星」的通配规则
         * （另有 {@code "/*.html"}、{@code "/*.js"}、{@code "/*.css"}、{@code "/*.png"} …
         * 一批同族通配规则），本意是放行静态资源，但 {@link org.springframework.util.AntPathMatcher}
         * 判定它们同样匹配 {@code /framework/list.shtml} 这类带后缀的 API 路径，导致
         * {@code LoginInterceptor} 对前端的所有请求都不做 token 校验。</p>
         *
         * <p>实测（AntPathMatcher，见下方 {@link #shtmlApiRequests_currentlyBypassAuthentication}）：</p>
         * <pre>
         *   /framework/list.shtml  → 被 "双星通配" 规则命中
         *   /project/list.shtml    → 被 "双星通配" 规则命中
         *   /logs/load.shtml      → 被 "双星通配" 规则命中
         *   /index.html            → 被 "单星 + .html" 等多条规则命中（本意：静态资源）
         * </pre>
         *
         * <p><b>处理决定（2026-10-15）</b>：经评估<b>不修复</b> —— 该行为是当前前端实际
         * 依赖的（Angular 7 + jQuery 打包产物硬编码了这些排除规则所放行的资源形态），
         * 收紧排除规则属于独立的安全加固工作，不在 Java 25 / Spring Boot 4.1 升级系列范围内。</p>
         *
         * <p><b>为何以 {@code @Disabled} 而非让测试持续红</b>：让一条已知且决定不修的断言
         * 长期红灯，会使后续每个升级变更的「契约测试全绿」验收都无法达成，
         * 红灯反而会被忽略。此处 SHALL 显式登记，且<b>不得被解读为「该场景已通过」</b>。</p>
         */
        @Test
        @Disabled("已知缺陷（已确认不修复）：excludePathPatterns 的通配规则使 .shtml API "
                + "路径绕过鉴权。当前实际行为由 shtmlApiRequests_currentlyBypassAuthentication 钉死")
        @DisplayName("未授权 SHALL 返回 9007（当前被 excludePathPatterns 通配规则绕过 —— 已知缺陷，不修复）")
        void unauthorized_returns9007AsJson() throws Exception {
            // /framework/list 语义上属于受保护端点；空 token 时 SHALL 被拦截并返回 9007
            mockMvc.perform(MockMvcRequestBuilders.get("/framework/list.shtml?curPage=1&pageSize=5")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("token", ""))
                    .andExpect(status().isOk())
                    .andExpect(result -> {
                        String body = result.getResponse().getContentAsString(StandardCharsets.UTF_8);
                        assertThat(body)
                                .as("拦截器 SHALL 返回 JSON 而非 HTML")
                                .doesNotContain("<html").doesNotContain("<!DOCTYPE");
                        assertThat(PARSER.readTree(body).get("code").asText())
                                .as("未授权 SHALL 返回 9007，实际=%s", body)
                                .isEqualTo("9007");
                    });
        }

        /**
         * 已登记缺陷的可执行证据：把「.shtml API 请求绕过鉴权」这一<b>当前实际行为</b>钉死。
         *
         * <p>本测试<b>不</b>断言「应当被拦截」（那是上面被 {@code @Disabled} 的那条），
         * 而是记录现状。这样做有两个作用：① 缺陷从「被忽略的红灯」变成「被记录的事实」；
         * ② 若将来 {@code excludePathPatterns} 被收紧、鉴权恢复生效，本测试会红，
         * 从而提醒同步处理上面那条被禁用���断言。</p>
         */
        @Test
        @DisplayName("【已知行为】.shtml API 请求当前绕过鉴权（已确认不修复，此处钉死现状）")
        void shtmlApiRequests_currentlyBypassAuthentication() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", "");

            JsonNode code = PARSER.readTree(body).path("code");
            assertThat(code.asText())
                    .as("记录当前行为：excludePathPatterns 的双星通配规则使 .shtml 请求绕过鉴权，"
                            + "空 token 仍返回 0000 而非 9007。若本测试失败，说明鉴权行为已变更，"
                            + "SHALL 同步复核 unauthorized_returns9007AsJson 上方的 @Disabled 说明")
                    .isEqualTo("0000");
        }

        @Test
        @DisplayName("失败响应的 code ∈ {9003,9004,9007,9999}")
        void failureCodes_areStandard() throws Exception {
            // 空参数触发 9003（Empty_Param）
            String body = mockMvc.perform(MockMvcRequestBuilders.get("/login/signin.shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("account", "")
                            .param("password", ""))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            String code = PARSER.readTree(body).get("code").asText();
            assertThat(code).isIn("0000", "9003", "9004", "9007", "9999");
        }
    }

    // ==================================================================
    // 场景④：日期格式与时区
    // ==================================================================

    @Nested
    @DisplayName("场景④ 日期格式与时区")
    class DateFormatAndTimeZone {

        @Test
        @DisplayName("日期字段匹配 ^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$，SHALL NOT 是纪元毫秒")
        void date_isFormattedNotTimestamp() throws Exception {
            // Project / ProjectVO 等实体带 createTime、modifyTime，由 MybatisPlusMetaObjectHandler 填充。
            // data.sql 未预置 project 行，故此处对 R 整体做形态断言：
            // 若 Jackson 序列化器把 java.util.Date 输出为纪元毫秒，响应中必然出现 10 位以上纯数字
            String body = get("/framework/list.shtml?curPage=1&pageSize=5", token);

            assertThat(body).as("响应 SHALL NOT 出现纪元毫秒时间戳形态")
                    .doesNotContainPattern("\"(createTime|modifyTime)\"\\s*:\\s*\\d{10,}");
        }

        @Test
        @DisplayName("时区固定为 GMT+8：同一时刻格式化后 SHALL 与期望偏移一致")
        void timeZone_isGmt8() throws Exception {
            // 用一个已知的本地时刻验证 GMT+8 语义（spring.jackson.time-zone: GMT+8）
            LocalDateTime utcInstant = LocalDateTime.of(2026, 1, 1, 0, 0, 0);
            LocalDateTime gmt8 = utcInstant.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("GMT+8"))
                    .toLocalDateTime();

            String formatted = gmt8.format(EXPECTED_DATE_FMT);

            assertThat(formatted).isEqualTo("2026-01-01 08:00:00");
            assertThat(formatted).matches(DATE);
            assertThat(formatted)
                    .as("相对 UTC 应偏移 +8 小时")
                    .isEqualTo(utcInstant.plusHours(8).format(EXPECTED_DATE_FMT));
        }

        @Test
        @DisplayName("日期格式常量与前端 bundle 中的格式串一致")
        void dateFormat_matchesFrontendBundle() {
            // 前端 main.js 中的格式串为 "yyyy-MM-dd HH:mm:ss"（nz-date-picker 默认格式），
            // 后端 spring.jackson.date-format 亦为同一格式 —— 两侧 SHALL 一致
            assertThat(EXPECTED_DATE_FMT.format(LocalDateTime.of(2026, 1, 2, 3, 4, 5)))
                    .as("格式化器 SHALL 输出前端约定的格式")
                    .isEqualTo("2026-01-02 03:04:05");
            assertThat(LocalDateTime.of(2026, 1, 2, 3, 4, 5).format(EXPECTED_DATE_FMT))
                    .matches(DATE);
        }
    }

    // ==================================================================
    // 场景⑤：null 字段保留
    // ==================================================================

    @Nested
    @DisplayName("场景⑤ null 字段保留")
    class NullFieldRetention {

        @Test
        @DisplayName("值为 null 的字段 SHALL 出现在 JSON 中，SHALL NOT 被省略")
        void nullFields_areRetained() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=50", token);

            JsonNode record = firstRecord(body);
            assertThat(record).isNotNull();

            // 遍历该 VO 的全部属性：SHALL NOT 出现「Java 侧有值、JSON 侧缺键」。
            // 字段清单 = FrameworkSummaryVO 声明的 7 字段（M5-A1 起；此前的 8 字段
            // 含 password 已由 api-data-object 的「凭据字段 SHALL NOT 出现在任何响应」
            // Requirement 移除 —— 凭据字段根本不参与 null 保留规则）。
            for (String field : List.of("id", "code", "name", "description",
                    "gitHome", "account", "isPublic")) {
                assertThat(record.has(field))
                        .as("字段 %s SHALL 保留在 JSON 中（即使为 null），SHALL NOT 被省略", field)
                        .isTrue();
            }
        }

        @Test
        @DisplayName("顶层 data 为 null 时该键 SHALL NOT 省略")
        void topLevelData_null_isRetained() throws Exception {
            // /login/signin 传入格式合法但不匹配的账户 → LoginCtrl 走 R.success() 且 data 为 null
            String body = mockMvc.perform(MockMvcRequestBuilders.get("/login/signin.shtml")
                            .header("x-requested-with", "XMLHttpRequest")
                            .param("account", "contract-no-such-account")
                            .param("password", "irrelevant"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            assertThat(body).as("响应 SHALL 含 data 键，实际=%s", body).contains("\"data\"");
            assertThat(PARSER.readTree(body).has("data"))
                    .as("data 为 null 时该键 SHALL 仍然存在")
                    .isTrue();
            assertThat(PARSER.readTree(body).get("data").isNull())
                    .as("该场景下 data 的值 SHALL 为 null")
                    .isTrue();
        }
    }

    // ==================================================================
    // 场景⑥：WebSocket 端点可达
    // ==================================================================

    @Nested
    @DisplayName("场景⑥ WebSocket 端点带后缀可达")
    class WebSocketEndpoint {

        @Test
        @DisplayName("/websocket.shtml 不被静态资源处理器吞掉")
        void websocketPath_notSwallowedByStaticResources() throws Exception {
            // MOCK 环境下无法建立真实 WS 连接（无嵌入式容器监听端口）。
            // 可验证的是：该路径 SHALL NOT 被 addResourceHandler("/**") 当作静态资源处理，
            // 即响应 SHALL NOT 为静态资源的 200，而应是 404（无 REST 端点注册）或非静态内容。
            MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(FrontendEndpointCatalog.websocketPath()))
                    .andReturn();

            int status = result.getResponse().getStatus();
            String contentType = result.getResponse().getContentType();

            assertThat(status)
                    .as("WebSocket 端点由 JSR-356 处理器接管，SHALL NOT 返回静态资源内容")
                    .isNotEqualTo(200);
            assertThat(String.valueOf(contentType))
                    .as("SHALL NOT 是静态资源的内容类型")
                    .doesNotContain("text/css", "application/javascript");
        }

        @Test
        @DisplayName("WebSocket 端点在后端确实以 .shtml 注册")
        void websocketEndpoint_declaredWithSuffix() {
            // 该端点由 @ServerEndpoint(value = "/websocket.shtml") 声明，
            // 不经过 Spring MVC 路由，因此不受 PathMatchConfigurer 变更影响
            assertThat(FrontendEndpointCatalog.websocketPath()).endsWith(".shtml");
        }
    }

    // ==================================================================
    // 场景⑦：分页列表接口应使用 PageVO<XxxVO>（api-data-object capability 的 PageVO 分页约定）
    // ==================================================================

    /**
     * 该组关注的端点是 {@code /project/list} —— M3-C 改造后它切到
     * {@code PageVO<ProjectSummaryVO>}（5 字段投影，详见
     * {@code openspec/changes/2026-10-17-project-vo-trim/}）。
     * 契约断言通过 {@code data.records}（PageVO 字段）来钉死真实形状；
     * 字段名错位会立即变红。
     */
    @Nested
    @DisplayName("场景⑦ 分页列表接口应使用 PageVO<ProjectSummaryVO>（records 字段非空且元素为 ProjectSummaryVO）")
    class ProjectListPageVoContract {

        /**
         * 与 db/data.sql 中 contract-demo 种子对应的 19 位雪花编码。
         * M3-C：ProjectSummaryVO 包含 code / name / englishName / state /
         * createTime 共 5 字段；裁切掉的 description / databaseType 等
         * 不会出现在 records 元素中。
         */
        private static final String SEED_PROJECT_CODE = "1882812765213425001";

        @Test
        @DisplayName("data.records 是非空数组，首项含 ProjectSummaryVO 关键字段且值非空")
        void project_list_records_containProjectVOFields() throws Exception {
            String body = get("/project/list.shtml?curPage=1&pageSize=10", token);

            JsonNode records = PARSER.readTree(body).path("data").path("records");
            assertThat(records.isArray())
                    .as("/project/list 响应的 data 节点下 SHALL 出现 records 数组（PageVO 字段名），实际=%s",
                            records)
                    .isTrue();
            assertThat(records)
                    .as("contract-test 账户下 project 种子 SHALL 至少返回 1 条")
                    .isNotEmpty();

            JsonNode first = records.get(0);
            // 4 个关键字段必现，且值 SHALL 与种子一致（api-contract 能力要求 null 字段保留，
            // 所以 "字段存在" 不能区分 "正确赋值" 与 "字段值为 null"——必须用 asText 校验值）。
            assertThat(first.get("code").asText())
                    .as("records[0].code SHALL 等于 db/data.sql 种子编码")
                    .isEqualTo(SEED_PROJECT_CODE);
            assertThat(first.get("name").asText()).isEqualTo("contract-demo");
            assertThat(first.get("englishName").asText()).isEqualTo("contract-demo");
            assertThat(first.has("createTime")).isTrue();
        }

        @Test
        @DisplayName("data.records 元素字段集合 ⊆ ProjectSummaryVO 5 字段（钉死 M3-C 裁切生效）")
        void project_list_records_fieldSet_isSuperSetOfProjectVO() throws Exception {
            String body = get("/project/list.shtml?curPage=1&pageSize=10", token);

            JsonNode records = PARSER.readTree(body).path("data").path("records");
            assertThat(records).isNotEmpty();

            // M3-C 裁切后，列表 VO 仅有 5 字段（code / name / englishName /
            // state / createTime）；M3-A-2 的 22 字段全集合已经收窄。
            // 反向：records[0] 实际字段集合 SHALL NOT 出现 ProjectSummaryVO
            // 持久化字段以外的字段。
            java.util.Set<String> allowed = java.util.Set.of(
                    "code", "name", "englishName", "state", "createTime"
            );
            java.util.Set<String> actual = fieldNames(records.get(0));
            assertThat(actual)
                    .as("records[0] SHALL NOT 出现 ProjectSummaryVO 持久化字段以外的字段，实际=%s", actual)
                    .isSubsetOf(allowed);

            // 反向：核心持久化字段 SHALL 存在 —— 防止装配断章。
            assertThat(actual).contains("code", "name", "englishName");
        }

        @Test
        @DisplayName("data.curPage / pageSize / totalPage SHALL 回填实际查询参数（钉死非假分页）")
        void project_list_reflectsPaginationParams() throws Exception {
            // 本组钉住 `2026-10-18-project-list-real-pagination` 修的"假分页"缺陷：
            // 该缺陷下 list() 忽略 curPage/pageSize 拉全量，且从不 set 三个分页字段
            // （恒为 int 默认 0），前端算出的翻页边界全部失效。
            String body = get("/project/list.shtml?curPage=1&pageSize=10", token);

            JsonNode data = PARSER.readTree(body).path("data");
            assertThat(data.path("curPage").asInt())
                    .as("data.curPage SHALL 回填实际查询参数（期望 1），实际=%s", data.path("curPage"))
                    .isEqualTo(1);
            assertThat(data.path("pageSize").asInt())
                    .as("data.pageSize SHALL 回填实际查询参数（期望 10），实际=%s", data.path("pageSize"))
                    .isEqualTo(10);

            long totalRow = data.path("totalRow").asLong();
            if (totalRow > 0) {
                int expectedTotalPage = (int) Math.ceil((double) totalRow / 10);
                assertThat(data.path("totalPage").asInt())
                        .as("data.totalPage SHALL 等于 ceil(totalRow / pageSize)=%d，SHALL NOT 为 0；"
                                + "totalRow=%d", expectedTotalPage, totalRow)
                        .isEqualTo(expectedTotalPage);
            }
        }

        @Test
        @DisplayName("curPage / pageSize 缺省或非法时 SHALL 回退默认值（1 / 10），SHALL NOT 报错")
        void project_list_fallsBackToDefaultsWhenParamsInvalid() throws Exception {
            String body = get("/project/list.shtml?curPage=0&pageSize=-1", token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("非法入参 SHALL NOT 让响应失败（仍 R.success），实际=%s", root)
                    .isTrue();

            JsonNode data = root.path("data");
            assertThat(data.path("curPage").asInt())
                    .as("curPage=0 SHALL 回退到默认 1，实际=%s", data.path("curPage"))
                    .isEqualTo(1);
            assertThat(data.path("pageSize").asInt())
                    .as("pageSize=-1 SHALL 回退到默认 10，实际=%s", data.path("pageSize"))
                    .isEqualTo(10);
        }
    }

    // ==================================================================
    // 场景⑧：受保护端点 SHALL 走 R<T> 包装而非裸实体（api-contract capability 的 R 包络约束）
    // ==================================================================

    /**
     * `loadByCode` 是本能力落地的第一个"被加固"的端点：原本直返 `ProjectVO`
     * 在 M3-A 变更为 `R<ProjectVO>`。该 Scenario 组为这条加固提供契约守护与
     * 反向验证 —— 一旦后续回归把 loadByCode 改回直返，本 Nested 立刻变红。
     *
     * 详见 {@code openspec/changes/2026-10-18-project-load-by-code-vo-contract/}。
     */
    @Nested
    @DisplayName("场景⑧ 受保护端点走 R<T> 包装（loadByCode 应返 R<ProjectDetailVO>）")
    class LoadByCodeReturnsWrappedVO {

        /**
         * 与 db/data.sql 中 contract-demo 种子对应的 19 位雪花编码。
         * M3-C 改造：loadByCode 现在返 R<ProjectDetailVO>（12 字段投影）而非
         * 原 22 字段 ProjectVO。下方"正常路径"用例断言 description 字段
         * 强等值 —— description 是 ProjectDetailVO 独有字段（ProjectSummaryVO
         * 不含），如 M3-C 漏改，Controller 仍返 ProjectVO 不带 description 字段
         * 类型，断言会立刻失败。
         */
        private static final String SEED_PROJECT_CODE = "1882812765213425001";
        private static final String SEED_PROJECT_DESCRIPTION = "契约回归测试种子项目";

        @Test
        @DisplayName("正常路径下 data 字段是 ProjectDetailVO 形态，含 code / name / englishName / description 等")
        void loadByCode_returns_R_wrapped_ProjectVO() throws Exception {
            // 注：loadByCode 不需要 token，但本测试仍以 token 调用以维持与其他 contract 测试一致
            String body = get(
                    "/project/load/code/" + SEED_PROJECT_CODE + ".shtml",
                    token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("R 包络的 success 字段 SHALL 为 true，实际=%s", root.path("success"))
                    .isTrue();
            assertThat(root.path("code").asText())
                    .as("R 包络的 code 字段 SHALL 等于 0000，实际=%s", root.path("code"))
                    .isEqualTo("0000");

            JsonNode data = root.path("data");
            assertThat(data.isObject())
                    .as("data 字段 SHALL 是 ProjectDetailVO 对象而非空/null，实际=%s", data)
                    .isTrue();
            // M3-C 反向验证：用 description 强等值（ProjectDetailVO 12 字段之一；
            // ProjectSummaryVO 7 字段不含此字段）。如 M3-C 漏改返 22 字段 ProjectVO，
            // data.description 仍存在 —— 但 ProjectVO 已从代码中删除，本断言
            // 还同时验证 description 字符串值映射正确。
            assertThat(data.path("description").asText())
                    .as("data.description SHALL 等于种子描述'契约回归测试种子项目'，"
                            + "如 M3-C 漏改返旧 ProjectVO 形态，该断言仍可能通过；"
                            + "真正的反向验证见 task 4.4（add 22 字段断言）")
                    .isEqualTo(SEED_PROJECT_DESCRIPTION);
            assertThat(data.path("code").asText())
                    .as("data.code SHALL 等于种子编码；如为 'undefined'/'null'，说明 Controller 改回裸实体")
                    .isEqualTo(SEED_PROJECT_CODE);
            assertThat(data.path("name").asText())
                    .as("data.name SHALL 等于种子项目名 contract-demo，实际=%s", data.path("name"))
                    .isEqualTo("contract-demo");
            assertThat(data.path("englishName").asText())
                    .as("data.englishName SHALL 等于种子英文名 contract-demo")
                    .isEqualTo("contract-demo");

            // M3-C 反向验证：被裁切的字段 SHALL NOT 出现在 data 中。
            // ProjectDetailVO 不含 databaseType / language / state / sqlFile /
            // downloadUrl / isRepository / isParseTable / isParseClass / isIncrement
            // / id。data 含任意一个都说明返回了旧 22 字段 ProjectVO 或实体全集。
            java.util.Set<String> allowed = java.util.Set.of(
                    "code", "name", "englishName", "description", "copyright", "author",
                    "phone", "basePackage", "buildNumber", "createTime", "updateTime", "accountCode"
            );
            java.util.Set<String> actual = fieldNames(data);
            assertThat(actual)
                    .as("data SHALL NOT 出现 ProjectDetailVO 12 字段以外的字段（裁切生效），"
                            + "实际=%s", actual)
                    .isSubsetOf(allowed);
        }

        @Test
        @DisplayName("找不到 code 时仍返 R.success(null) 而非 5xx（null 早返路径）")
        void loadByCode_when_code_notFound_returns_R_success_null() throws Exception {
            String body = get(
                    "/project/load/code/__no-such-code-9999.shtml",
                    token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("R 包络的 success 字段 SHALL 为 true（找不到时也是成功而非 500），实际=%s",
                            root.path("success"))
                    .isTrue();
            assertThat(root.path("code").asText()).isEqualTo("0000");
            // data 字段 SHALL 存在但实际为 null（api-contract line 82：data 为空时 SHALL 以 null 呈现）
            assertThat(root.has("data"))
                    .as("R 包络 SHALL 保留 data 键，实际节点=%s", root)
                    .isTrue();
            assertThat(root.path("data").isNull())
                    .as("data 字段 SHALL 为 null，实际=%s", root.path("data"))
                    .isTrue();
        }
    }

    // ==================================================================
    // 场景⑩：框架技术池端点 SHALL NOT 泄漏 Git 口令（api-data-object 凭据字段约束）
    // ==================================================================

    /**
     * `Frameworks` 实体持有模板仓库的 Git 账号（{@code account}）与口令（{@code password}）。
     * M5-A1 之前 {@code /framework/list} 与 {@code /framework/load} 把实体直出，两者都把
     * {@code password} 明文写进 HTTP 响应 —— 与 M3-D 堵掉的 {@code AccountVO.password} 同类漏洞。
     *
     * <p>本组同时钉两件事：① 凭据字段不出现在响应；② 分页容器是 {@code PageVO}
     * （{@code records}）而非 {@code Page}（{@code voList}）。</p>
     *
     * <p>详见 {@code openspec/changes/archive/2026-10-18-framework-list-vo-contract/}。</p>
     */
    @Nested
    @DisplayName("场景⑩ 框架技术池端点不泄漏 Git 口令，且分页容器为 PageVO")
    class FrameworkListNeverLeaksGitPassword {

        /** db/data.sql 中框架种子 code（springboot2.1.6 模板）。 */
        private static final String SEED_FRAMEWORK_CODE = "1882812765213425664";

        /** db/data.sql 中该框架的 Git 口令真实值 —— 反向验证的证据。 */
        private static final String SEED_FRAMEWORK_PASSWORD = "888888";

        /** FrameworkSummaryVO 声明的 7 字段（不含 password、不含 BaseVO 的 4 个查询辅助字段）。 */
        private static final java.util.Set<String> FRAMEWORK_VO_FIELDS = java.util.Set.of(
                "id", "code", "name", "description", "gitHome", "account", "isPublic");

        @Test
        @DisplayName("列表端点 data.records[*] 字段集合 == FrameworkSummaryVO 的 7 字段")
        void frameworkList_recordsFieldSet_equalsSummaryVO() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=10", token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("/framework/list 的 R.success 字段 SHALL 为 true，实际=%s", root.path("success"))
                    .isTrue();

            JsonNode data = root.path("data");
            assertThat(data.isObject()).as("data SHALL 是 PageVO 对象").isTrue();

            // 容器形态：records 而非 voList（M2-1 收敛方向，本变更把 /framework/list 补齐）
            assertThat(data.has("records"))
                    .as("data SHALL 含 records 数组（PageVO 字段），实际字段=%s", data)
                    .isTrue();
            assertThat(data.has("voList"))
                    .as("data SHALL NOT 含 voList（旧 Page 字段），实际字段=%s", data)
                    .isFalse();

            JsonNode records = data.path("records");
            assertThat(records.isArray()).as("records SHALL 是数组").isTrue();
            assertThat(records).as("框架种子 SHALL 至少 1 条").isNotEmpty();

            // 逐条校验字段集合
            for (JsonNode record : records) {
                java.util.Set<String> actual = fieldNames(record);
                assertThat(actual)
                        .as("records[*] SHALL 只含 FrameworkSummaryVO 的 7 字段，实际=%s", actual)
                        .isSubsetOf(FRAMEWORK_VO_FIELDS);
            }
        }

        @Test
        @DisplayName("列表端点 records[*] SHALL NOT 含 password 与 BaseVO 的 4 个查询辅助字段")
        void frameworkList_neverLeaksPasswordOrBaseVOFields() throws Exception {
            String body = get("/framework/list.shtml?curPage=1&pageSize=10", token);

            // 明文密码不得出现（db/data.sql 中该框架的 password 真实值）
            assertThat(body)
                    .as("/framework/list 响应 SHALL NOT 含 db/data.sql 中的 Git 口令明文（%s）",
                            SEED_FRAMEWORK_PASSWORD)
                    .doesNotContain("\"password\"");
            assertThat(body)
                    .as("/framework/list 响应 SHALL NOT 含 password 键")
                    .doesNotContain("password");

            // BaseVO 的 4 个查询辅助字段不得进入 **records 元素**。
            // 注意：容器 data 本身是 PageVO<VO>，而 PageVO 也 extends BaseVO ——
            // 那是全仓库所有分页端点的共性问题（/project/list、/account/list 同样），
            // 属独立议题（见本变更 design 的 Open Questions）；本断言只覆盖元素层。
            JsonNode records = PARSER.readTree(body).path("data").path("records");
            assertThat(records.isArray()).as("records SHALL 是数组").isTrue();
            assertThat(records).as("框架种子 SHALL 至少 1 条").isNotEmpty();
            for (JsonNode record : records) {
                java.util.Set<String> elementFields = fieldNames(record);
                for (String derived : java.util.List.of(
                        "createTimeBegin", "createTimeEnd", "updateTimeBegin", "updateTimeEnd")) {
                    assertThat(elementFields)
                            .as("records[*] SHALL NOT 含 BaseVO 查询辅助字段 %s，实际字段=%s",
                                    derived, elementFields)
                            .doesNotContain(derived);
                }
            }
        }

        @Test
        @DisplayName("详情端点 data 字段集合 == FrameworkSummaryVO 的 7 字段，不含 password")
        void frameworkLoad_dataFieldSet_equalsSummaryVO() throws Exception {
            String body = get("/framework/load.shtml?code=" + SEED_FRAMEWORK_CODE, token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("/framework/load 的 R.success 字段 SHALL 为 true，实际=%s", root.path("success"))
                    .isTrue();

            JsonNode data = root.path("data");
            assertThat(data.isObject())
                    .as("/framework/load 的 data SHALL 是 FrameworkSummaryVO 对象（找不到时为 null），实际=%s", data)
                    .isTrue();
            assertThat(data.path("code").asText())
                    .as("data.code SHALL 等于种子技术编码（证明查到记录而非空对象）")
                    .isEqualTo(SEED_FRAMEWORK_CODE);

            java.util.Set<String> actual = fieldNames(data);
            assertThat(actual)
                    .as("/framework/load 的 data SHALL 只含 7 字段，实际=%s", actual)
                    .isSubsetOf(FRAMEWORK_VO_FIELDS);
            assertThat(actual.contains("password"))
                    .as("data 字段集合 SHALL NOT 含 password —— 实际=%s", actual)
                    .isFalse();
        }
    }

    // ==================================================================
    // 辅助方法
    // ==================================================================

    /**
     * 列出 JSON 节点的所有顶层字段名，仅用于契约断言的诊断输出。
     * Jackson 3 下 {@code fieldNames()} 返回 {@code Set<String>}；本工具仅在断言失败时使用。
     */
    private static java.util.Set<String> fieldNames(JsonNode node) {
        java.util.LinkedHashSet<String> names = new java.util.LinkedHashSet<>();
        if (node != null && node.isObject()) {
            node.fieldNames().forEachRemaining(names::add);
        }
        return names;
    }

    /**
     * 从导出契约里切出一个组件 schema（{@code components.schemas.<name>}）的完整片段。
     *
     * <p><b>⚠️ 找「下一个 schema 头」必须用行首锚定的正则</b>，不能用
     * {@code indexOf("\n    ")} 或 {@code indexOf("\n    [A-Z]")}：schema 内部的属性行是
     * <b>8 空格</b>缩进，而 4 / 5 空格的字符串是它的<b>前缀</b> —— {@code indexOf} 会匹配到
     * 第一个属性行、把片段截断成「只有 schema 名」，于是字段检查全部落空、断言
     * <b>假通过</b>。</p>
     *
     * <p>该陷阱在本文件被踩过<b>三次</b>（fieldBlock / 场景⑯ 扫描断言 / 场景⑰），
     * 故统一收敛到本方法，SHALL NOT 再在别处手写。</p>
     */
    private static String schemaBlock(String yaml, String schemaName) {
        int i = yaml.indexOf("\n    " + schemaName + ":");
        assertThat(i).as("契约 SHALL 含 schema %s", schemaName).isNotNegative();
        int from = i + 1;
        java.util.regex.Matcher next = java.util.regex.Pattern
                .compile("(?m)^    [A-Z][A-Za-z0-9_]*:\s*$")
                .matcher(yaml);
        int to = next.find(from) ? next.start() : yaml.length();
        return yaml.substring(from, to);
    }

    private String get(String path, String jwt) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.get(path)
                                .header("x-requested-with", "XMLHttpRequest")
                                .param("token", jwt))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /**
     * POST 表单参数型端点（写侧）。
     *
     * <p>本项目写侧端点未用 {@code @RequestBody}，而是把每个字段作为独立的
     * query / form 参数接收（见 {@code ProjectController#build} 的显式
     * {@code @RequestParam} 形参）。故按 query string 拼接。</p>
     */
    private String post(String path, String formParams, String jwt) throws Exception {
        return mockMvc.perform(
                        MockMvcRequestBuilders.post(path)
                                .header("x-requested-with", "XMLHttpRequest")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .param("token", jwt)
                                .content(formParams))
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
    }

    /**
     * 取出列表接口的第一条记录。
     *
     * <p>本项目的分页包装类是 {@code com.aicode.core.Page}，其列表字段名为
     * {@code voList}（不是 MyBatis-Plus 的 {@code records}），
     * 计数字段为 {@code totalRow}。契约断言 SHALL 反映真实结构。</p>
     */
    /**
     * 取出列表接口的第一条记录。
     *
     * <p><b>容器字段名兼容</b>：M2-1 把 {@code /project/list} 从 {@code Page}（{@code voList}）
     * 收敛为 {@code PageVO}（{@code records}）；M5-A1 把 {@code /framework/list} 补齐到同一形态。
     * 本 helper 同时识别两种字段名，使场景①（标识符序列化，作用于 {@code /framework/list}）
     * 在容器收敛前后都可用。</p>
     */
    private JsonNode firstRecord(String body) throws Exception {
        JsonNode data = PARSER.readTree(body).path("data");
        JsonNode list = data.has("records") ? data.path("records") : data.path("voList");
        return (list.isArray() && list.size() > 0) ? list.get(0) : null;
    }


    // ==================================================================
    // 场景⑨：账户加载端点 SHALL 走 R<AccountPublicVO> 且不含 password 字段（M3-D 防护）
    // ==================================================================

    /**
     * `AccountController.loadByCode` 是 M3-D 落地的端点：返 R<AccountPublicVO>（3 字段
     * id / code / account，**不含** password）。本 Scenario 组为这条加固提供契约守护与
     * 反向验证 —— 一旦后续回归把 password 字段暴露给前端，本 Nested 立刻变红。
     *
     * 详见 {@code openspec/changes/2026-10-17-account-load-and-menu/}。
     */
    @Nested
    @DisplayName("场景⑨ 账户加载端点走 R<AccountPublicVO>（不含 password 字段）")
    class AccountLoadReturnsAccountPublicVO {

        /**
         * 与 db/data.sql 中 contract-test 账户对应的字面编码（accountCode）。
         * M3-D 实施：loadByCode 端点不再要求前端传 code —— 后端从 token claim
         * 抽 accountCode 自己查账户，所以本测试直接用同一个 mock 但走 query
         * 路径。详见 {@code AccountController.loadByCode}。
         */
        private static final String SEED_ACCOUNT_CODE = "21218cca77804d2ba2";

        @Test
        @DisplayName("正常路径下 data 字段是 AccountPublicVO 形态，含 id / code / account 不含 password")
        void accountLoad_returns_R_wrapped_AccountPublicVO() throws Exception {
            String body = get("/account/load.shtml?code=" + SEED_ACCOUNT_CODE, token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("R 包络的 success 字段 SHALL 为 true，实际=%s", root.path("success"))
                    .isTrue();
            assertThat(root.path("code").asText())
                    .as("R 包络的 code 字段 SHALL 等于 0000，实际=%s", root.path("code"))
                    .isEqualTo("0000");

            JsonNode data = root.path("data");
            assertThat(data.isObject())
                    .as("data 字段 SHALL 是 AccountPublicVO 对象而非空/null，实际=%s", data)
                    .isTrue();
            // 正常字段断言
            assertThat(data.path("code").asText())
                    .as("data.code SHALL 等于 seed 编码（1882812765213425002）")
                    .isEqualTo(SEED_ACCOUNT_CODE);
            // M3-D 反向验证：data SHALL NOT 含 password 字段 —— 即便后端
            // 错误地改成 AccountVO 形态（含 password）也立刻被该断言挡下
            java.util.Set<String> allowed = java.util.Set.of("id", "code", "account");
            java.util.Set<String> actual = fieldNames(data);
            assertThat(actual)
                    .as("data SHALL NOT 出现 AccountPublicVO 3 字段以外的字段（password 防护）"
                            + "，实际=%s", actual)
                    .isSubsetOf(allowed);
            // 显式断言 password 字段绝对不在
            assertThat(actual.contains("password"))
                    .as("data 字段集合 SHALL NOT 含 password —— M3-D 防护重点")
                    .isFalse();
        }
    }

    // ==================================================================
    // 场景⑪：写侧端点 SHALL 返回写侧 VO 而非实体全集
    // （2026-10-18-project-create-and-sql-init 防护）
    // ==================================================================

    /**
     * 本组守护 {@code api-data-object} 的「Controller SHALL 返回 VO 而非持久化实体」
     * 在**写侧**的落地。此前 `POST /project/build` 返 `R<Project>`（29 字段，含 7 个
     * 恒为 null 的关联 list 与 9 个纯内部字段），`POST /project/sql/build` 返
     * `R<ProjectSql>` —— 且后者的「VO」字段与实体完全相同（含 `id`），
     * 等于只改了类名没做裁切。
     *
     * <p>反向验证：把任一端点的返回类型改回实体（或把 `id` 加回 VO），
     * 本 Nested 立即变红。</p>
     */
    @Nested
    @DisplayName("场景⑪ 写侧端点返写侧 VO 而非实体全集")
    class WriteSideVoContract {

        /** `ProjectCreateVO` 的 8 字段白名单。 */
        private static final java.util.Set<String> CREATE_VO_FIELDS = java.util.Set.of(
                "code", "name", "englishName", "author", "basePackage",
                "databaseType", "state", "createTime"
        );

        /** `ProjectSqlVO` 的 4 字段白名单。 */
        private static final java.util.Set<String> SQL_VO_FIELDS = java.util.Set.of(
                "code", "projectCode", "tsql", "state"
        );

        /**
         * 本组用例会**真实创建**项目与 SQL 记录（写侧端点的正向行为无法靠打桩验证）。
         *
         * <p><b>必须清理</b>：{@code ApiContractIT} 共享一个 Spring 上下文，测试库不随
         * 用例重置。若不清理，新建的 `it_write_vo_*` 项目会与 {@code db/data.sql} 的
         * {@code contract-demo} 种子<b>同属一个账户</b>，而 {@code /project/list} 按
         * {@code createTime DESC} 排序 —— 新建的更「新」，会把种子挤出 {@code records[0]}，
         * 直接让场景⑦ 的「首项 code 等于种子」断言变红。</p>
         *
         * <p>这正是「测试间相互独立，禁共享可变状态」（AGENTS §7.5）要防的情形。</p>
         */
        @org.junit.jupiter.api.AfterEach
        void cleanUpCreatedData() {
            ProjectSqlMapper sqlMapper = wac.getBean(ProjectSqlMapper.class);
            ProjectMapper projectMapper = wac.getBean(ProjectMapper.class);

            // 先删 SQL（外键语义：SQL 段挂在项目下），再删项目
            sqlMapper.delete(new LambdaQueryWrapper<ProjectSql>()
                    .likeRight(ProjectSql::getProjectCode, "it_write_vo"));
            // englishName 是本次建项目唯一可控的标识前缀
            projectMapper.delete(new LambdaQueryWrapper<Project>()
                    .likeRight(Project::getEnglishName, "it_write_vo"));
        }

        /**
         * 建项目的完整表单参数（9 个必填 + token）。每次调用用唯一的 englishName ——
         * `ProjectServiceImpl.save()` 校验 `englishName` 全局唯一（它同时是
         * 数据库 schema 名），重复会抛 `Exists`。
         */
        private String createProjectBody(String englishName) throws Exception {
            return post("/project/build.shtml",
                    "name=契约演示项目"
                            + "&englishName=" + englishName
                            + "&author=契约守护"
                            + "&phone=13800000000"
                            + "&basePackage=com.demo.contract"
                            + "&copyright=Copyright (c) 2026"
                            + "&description=场景十一写侧 VO 契约守护"
                            + "&databaseType=Mysql"
                            + "&language=Java",
                    token);
        }

        @Test
        @DisplayName("POST /project/build 的 data 字段集合 SHALL ⊆ ProjectCreateVO 8 字段")
        void projectBuild_data_isProjectCreateVo_notEntity() throws Exception {
            String body = createProjectBody("it_write_vo_1");

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("建项目 SHALL 成功（实际=%s）", root.path("info"))
                    .isTrue();

            java.util.Set<String> actual = fieldNames(root.path("data"));
            assertThat(actual)
                    .as("data 字段集合 SHALL NOT 出现 ProjectCreateVO 8 字段以外的字段"
                            + "（即 SHALL NOT 是 Project 实体的 29 字段全集），实际=%s", actual)
                    .isSubsetOf(CREATE_VO_FIELDS);

            // 反向验证点名：实体内部字段一个都不许出现
            for (String internal : java.util.List.of("id", "sqlFile", "downloadUrl", "buildNumber",
                    "isRepository", "isParseTable", "isParseClass", "isIncrement",
                    "relationshipAndDisplay")) {
                assertThat(actual.contains(internal))
                        .as("data 字段集合 SHALL NOT 含实体内部字段 '%s'，实际=%s", internal, actual)
                        .isFalse();
            }
            // 7 个关联 list 同样不许出现
            for (String association : java.util.List.of("projectFramworkList", "projectMapList",
                    "projectJobList", "projectModuleList", "projectRepositoryAccountList",
                    "projectSqlList")) {
                assertThat(actual.contains(association))
                        .as("data 字段集合 SHALL NOT 含关联 list '%s'（创建时恒为 null），实际=%s",
                                association, actual)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("POST /project/build 返回的 data.code SHALL 非空（业务编码由雪花生成）")
        void projectBuild_data_carriesBusinessCode() throws Exception {
            // 正向行为断言：VO 装配不能把业务编码弄丢 ——
            // 主键 id 由数据库自增，业务编码 code 由 Hutool 雪花生成，
            // 二者相互独立（见 AGENTS §5.6）。若误把 id 当 code 返回，
            // 前端跳详情页会 404。
            String body = createProjectBody("it_write_vo_2");

            JsonNode data = PARSER.readTree(body).path("data");
            assertThat(data.path("code").asText())
                    .as("data.code SHALL 非空（前端据此跳详情页），实际=%s", data)
                    .isNotEmpty();
            assertThat(data.path("code").asText().length())
                    .as("data.code SHALL 是 19 位雪花业务编码而非自增主键，实际=%s",
                            data.path("code").asText())
                    .isEqualTo(19);
        }

        @Test
        @DisplayName("POST /project/sql/build 的 data 字段集合 SHALL == ProjectSqlVO 4 字段且不含 id")
        void projectSqlBuild_data_isProjectSqlVo_notEntity() throws Exception {
            String body = post("/project/sql/build.shtml",
                    "projectCode=1882812765213425001"
                            + "&tsql=CREATE TABLE demo_t (id BIGINT PRIMARY KEY);",
                    token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("导 SQL SHALL 成功（实际=%s）", root.path("info"))
                    .isTrue();

            java.util.Set<String> actual = fieldNames(root.path("data"));
            assertThat(actual)
                    .as("data 字段集合 SHALL ⊆ ProjectSqlVO 4 字段，实际=%s", actual)
                    .isSubsetOf(SQL_VO_FIELDS);
            assertThat(actual.contains("id"))
                    .as("data 字段集合 SHALL NOT 含自增主键 id —— 对外标识是业务编码 code，"
                            + "实际=%s", actual)
                    .isFalse();
            assertThat(root.path("data").path("projectCode").asText())
                    .as("data.projectCode SHALL 回显提交的 projectCode（前端据此确认写入归属）")
                    .isEqualTo("1882812765213425001");
        }

        @Test
        @DisplayName("POST /project/sql/modify 的 data SHALL 从持久化对象装配（不直返入参）")
        void projectSqlModify_data_assembledFromPersistedEntity() throws Exception {
            // 直接返入参会让响应缺 projectCode / state —— 入参只有 code + tsql。
            // 本用例经由 /load 造一条再改，断言响应含全部 4 字段。
            String sqlCode = PARSER.readTree(post("/project/sql/build.shtml",
                    "projectCode=1882812765213425001&tsql=CREATE TABLE demo_t2 (id BIGINT);",
                    token)).path("data").path("code").asText();

            String body = post("/project/sql/modify.shtml",
                    "code=" + sqlCode + "&tsql=CREATE TABLE demo_t2 (id BIGINT, name VARCHAR(32));",
                    token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("改 SQL SHALL 成功（实际=%s）", root.path("info"))
                    .isTrue();

            java.util.Set<String> actual = fieldNames(root.path("data"));
            assertThat(actual)
                    .as("modify 的 data SHALL ⊆ ProjectSqlVO 4 字段，实际=%s", actual)
                    .isSubsetOf(SQL_VO_FIELDS);
            assertThat(actual.contains("projectCode"))
                    .as("modify 的 data SHALL 含 projectCode —— 直返入参会缺此字段（入参只有 "
                            + "code + tsql），实际=%s", actual)
                    .isTrue();
            assertThat(root.path("data").path("tsql").asText())
                    .as("data.tsql SHALL 等于提交的新脚本")
                    .contains("name VARCHAR(32)");
        }
    }

    // ==================================================================
    // 场景⑫：映射实体端点 SHALL 用读投影 VO，组合结构 SHALL NOT 用 R<Object>
    // （2026-10-18-relationship-config 防护）
    // ==================================================================

    /**
     * 本组守护三件事：
     *
     * <ol>
     *   <li>{@code listMapClassTable} 的元素 SHALL 是 6 字段的 {@code MapClassTableVO}，
     *       SHALL NOT 含自增主键 {@code id} 与两个恒 null 的关联 list；</li>
     *   <li>{@code listMapFieldColumn} 的 {@code data} SHALL 是具名的
     *       {@code RelationshipFieldsVO}（3 个 key），SHALL NOT 是无约束的
     *       {@code {}} —— 该端点原先返 {@code R<Object>}，是本仓唯一一处；</li>
     *   <li>{@code PUT /modify} SHALL 走 {@code R<Boolean>}，SHALL NOT 裸返
     *       {@code boolean}（裸出参绕过 {@code R} 的 code/info/success，
     *       前端 {@code unwrapSuccess} 无从工作）。</li>
     * </ol>
     *
     * <p><b>反向验证</b>：把 {@code listMapFieldColumn} 改回 {@code R<Object>} →
     * {@code data} 的 key 断言红；把 {@code modify} 改回裸 {@code boolean} →
     * 场景⑫ 用例 3 红（此二者编译期不拦，<b>只有契约断言能抓到</b>）。</p>
     */
    @Nested
    @DisplayName("场景⑫ 映射实体端点用读投影 VO，组合结构不用 R<Object>")
    class RelationshipVoContract {

        private static final String REL_PROJECT_CODE = "1882812765213425001";

        /** `MapClassTableVO` 的 6 字段白名单。 */
        private static final java.util.Set<String> CLASS_TABLE_VO_FIELDS = java.util.Set.of(
                "code", "tableName", "className", "classModel", "dashedCaseName", "notes"
        );

        /** `MapFieldColumnReadVO` 的 8 字段白名单。 */
        private static final java.util.Set<String> FIELD_READ_VO_FIELDS = java.util.Set.of(
                "code", "mapClassTableCode", "column", "field", "fieldType",
                "isPrimaryKey", "isDate", "isState"
        );

        @Test
        @DisplayName("listMapClassTable 的 data[] SHALL 是 MapClassTableVO 6 字段")
        void listMapClassTable_data_isMapClassTableVo() throws Exception {
            String body = get("/project/relationship/listMapClassTable.shtml?projectCode="
                    + REL_PROJECT_CODE, token);

            JsonNode root = PARSER.readTree(body);
            // 种子项目若尚未解析出表，本用例无对象可校验 —— 明确跳过而非误报
            if (!root.path("success").asBoolean()) {
                org.junit.jupiter.api.Assumptions.assumeTrue(false,
                        "种子项目尚未 init 解析出表，本用例无对象可校验；"
                                + "VO 投影由 listMapFieldColumn 与下面的 schema 断言覆盖");
            }

            JsonNode data = root.path("data");
            assertThat(data.isArray())
                    .as("data SHALL 是数组，实际=%s", data)
                    .isTrue();
            if (data.size() == 0) {
                return;
            }

            java.util.Set<String> actual = fieldNames(data.get(0));
            assertThat(actual)
                    .as("data[0] 字段集合 SHALL ⊆ MapClassTableVO 6 字段（SHALL NOT 是 "
                            + "MapClassTable 实体的 9 字段），实际=%s", actual)
                    .isSubsetOf(CLASS_TABLE_VO_FIELDS);
            assertThat(actual.contains("id"))
                    .as("data[0] SHALL NOT 含自增主键 id —— 对外标识是业务编码 code，"
                            + "实际=%s", actual)
                    .isFalse();
            for (String association : java.util.List.of("mapFieldColumnList", "mapRelationshipList")) {
                assertThat(actual.contains(association))
                        .as("data[0] SHALL NOT 含关联 list '%s'（列表场景必为 null），实际=%s",
                                association, actual)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("listMapFieldColumn 的 data SHALL 是具名组合结构（3 个 key），SHALL NOT 空对象")
        void listMapFieldColumn_data_isNamedStructure_notEmptyObject() throws Exception {
            // 这是本组的核心断言：原实现返 R<Object>，契约里 data 是无约束的 {}，
            // 前端既拿不到 key 也不知道内部结构 —— 端点在契约层面不可消费。
            String body = get("/project/relationship/listMapFieldColumn.shtml"
                    + "?mapClassTableCode=not-exist-a&associateCode=not-exist-b", token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("该端点对未知编码 SHALL 仍成功（返回空候选）而非报错，实际=%s",
                            root.path("info"))
                    .isTrue();

            JsonNode data = root.path("data");
            assertThat(data.isObject())
                    .as("data SHALL 是对象（RelationshipFieldsVO），实际=%s", data)
                    .isTrue();
            java.util.Set<String> keys = fieldNames(data);
            assertThat(keys)
                    .as("data SHALL 恰好含 mainFields / associateFields / mapRelationship 三个 key"
                            + "（与后端原 jsonObject.put 的 key 一致，SHALL NOT 改名），实际=%s", keys)
                    .containsExactlyInAnyOrder("mainFields", "associateFields", "mapRelationship");

            // 字段候选的元素投影
            JsonNode mainFields = data.path("mainFields");
            assertThat(mainFields.isArray())
                    .as("mainFields SHALL 是数组，实际=%s", mainFields)
                    .isTrue();
            if (mainFields.size() > 0) {
                java.util.Set<String> f = fieldNames(mainFields.get(0));
                assertThat(f)
                        .as("mainFields[] 元素 SHALL ⊆ MapFieldColumnReadVO 8 字段，实际=%s", f)
                        .isSubsetOf(FIELD_READ_VO_FIELDS);
                assertThat(f.contains("displayAttribute"))
                        .as("mainFields[] 元素 SHALL NOT 含嵌套 displayAttribute（由 "
                                + "/displayAttribute/list 单独给出），实际=%s", f)
                        .isFalse();
            }
        }

        @Test
        @DisplayName("PUT /project/relationship/modify SHALL 走 R<Boolean> 而非裸 boolean")
        void relationshipModify_isWrappedInR() throws Exception {
            // 裸 boolean 绕过了 R 的 code/info/success 三元组 ——
            // 前端 unwrapSuccess 对 `true` 无从工作，该端点只能被特殊处理。
            // 该断言读的是导出契约的 schema（而非实际调用，因 modify 是 PUT + @RequestBody）。
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            assertThat(yaml).contains("/project/relationship/modify:");

            int idx = yaml.indexOf("/project/relationship/modify:");
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));
            assertThat(seg)
                    .as("modify 的响应 schema SHALL NOT 是裸 `type: boolean`"
                            + "（裸出参不在 R 包络内），实际片段=%s", seg)
                    .doesNotContain("type: boolean");
            assertThat(seg)
                    .as("modify 的响应 schema SHALL ref RBoolean，实际片段=%s", seg)
                    .contains("RBoolean");
        }

        @Test
        @DisplayName("listMapClassTable 的响应 schema SHALL ref RListMapClassTableVO 而非裸实体")
        void listMapClassTable_schema_isVoNotEntity() throws Exception {
            // 上一个用例在种子项目未 init 时会跳过（无 MapClassTable 数据），
            // 本用例在 **schema 层** 校验 VO 投影，因此无论有无数据都执行 ——
            // 保证「VO 投影」这条契约始终有守护，不留"靠数据运气"的空白。
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf("/project/relationship/listMapClassTable:");
            assertThat(idx)
                    .as("契约 SHALL 含 /project/relationship/listMapClassTable 端点")
                    .isNotNegative();
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));

            assertThat(seg)
                    .as("listMapClassTable 的响应 SHALL ref RListMapClassTableVO"
                            + "（6 字段投影），SHALL NOT ref 裸实体 RListMapClassTable，"
                            + "实际片段=%s", seg)
                    .contains("RListMapClassTableVO");
            assertThat(seg)
                    .as("listMapClassTable 的响应 SHALL NOT ref 裸实体，实际片段=%s", seg)
                    .doesNotContain("schemas/RListMapClassTable\"");
        }

        @Test
        @DisplayName("三个裸映射实体 schema SHALL NOT 再被任何响应引用")
        void noMappingEntitySchemaIsReferencedByResponse() throws Exception {
            // MapClassTable / MapRelationship / MapFieldColumn 三个实体的 schema
            // 仍会留在契约 components 里（其它端点可能用），但 SHALL NOT 有任何
            // 响应 $ref 指向它们 —— 那是"裸实体出参"的直接证据。
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            // 只看 paths 段（components 里的定义与 properties 引用不算）
            int pathsEnd = yaml.indexOf("\ncomponents:");
            String paths = pathsEnd > 0 ? yaml.substring(0, pathsEnd) : yaml;
            for (String entity : java.util.List.of("MapClassTable", "MapRelationship", "MapFieldColumn")) {
                assertThat(paths)
                        .as("paths 段 SHALL NOT 出现对裸实体 %s 的响应 $ref（SHALL 用 *VO / *ReadVO）"
                                + "；出现即说明该端点直出实体", entity)
                        .doesNotContain("schemas/RList" + entity + "\"");
            }
        }
    }

    // ==================================================================
    // 场景⑬：受约束字段 SHALL 在契约中以 enum 表达值域
    // （2026-10-18-display-attribute-write 防护）
    // ==================================================================

    /**
     * 本组守护 {@code api-data-object} 的
     * 「元驱动表单所需的展示属性 SHALL 完整出现在 VO 中」里的值域条款。
     *
     * <p><b>为什么必须测</b>：Java 注解属性只接受编译期常量 —— 无法写成对
     * {@code values()} 的引用（{@code static final String[]} 也不行，数组引用不是
     * 常量表达式）。所以 {@code @Schema(allowableValues = ...)} 的 24 个字面量是
     * <b>手写</b>的。新增枚举而漏改 {@code @Schema}，编译与运行都正常，
     * 只有本组能抓到 —— 且这是前端下拉的唯一数据来源，漏一个就少一个选项。</p>
     *
     * <p>反向验证：去掉 {@code displayType} 的 {@code allowableValues} →
     * 用例 1 失败（契约里退化为裸 {@code type: string}）。</p>
     */
    @Nested
    @DisplayName("场景⑬ 受约束字段在契约中以 enum 表达值域（displayType / matchType / fieldValidationMode）")
    class DisplayAttributeEnumContract {

        /** 读导出契约的原始 YAML。 */
        private String contractYaml() throws Exception {
            return mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        }

        /** 取出 `DisplayAttributeVO.<field>` 块（含其下的 enum 列表）。 */
        private String fieldBlock(String yaml, String field) {
            int voIdx = yaml.indexOf("\n    DisplayAttributeVO:");
            assertThat(voIdx)
                    .as("契约 SHALL 含 DisplayAttributeVO schema")
                    .isNotNegative();
            String voSeg = yaml.substring(voIdx);
            // 用行首锚定的正则找下一个「恰好 8 空格缩进的字段名行」——
            // 不能用 indexOf("\n        ")：10 空格的 "type:" / "enum:" 行也以
            // 8 空格为前缀，会被误判为下一个字段，导致 block 被截断成只剩字段名。
            java.util.regex.Matcher start = java.util.regex.Pattern
                    .compile("(?m)^        " + java.util.regex.Pattern.quote(field) + ":$")
                    .matcher(voSeg);
            assertThat(start.find())
                    .as("DisplayAttributeVO SHALL 含字段 %s", field)
                    .isTrue();
            int from = start.start();
            int fieldIdx = voSeg.indexOf("\n", from);
            java.util.regex.Matcher next = java.util.regex.Pattern
                    .compile("(?m)^        [a-zA-Z][a-zA-Z0-9]*:$")
                    .matcher(voSeg);
            int end = next.find(fieldIdx + 1) ? next.start() : voSeg.length();
            return voSeg.substring(fieldIdx, end);
        }

        @Test
        @DisplayName("displayType SHALL 带 enum 列表（24 个），SHALL NOT 是裸 type: string")
        void displayType_hasEnumInContract() throws Exception {
            String block = fieldBlock(contractYaml(), "displayType");

            // 注意：OpenAPI 的正确形态是 `type: string` + `enum: [...]` 共存 ——
            // 所以「非裸 string」不能用「不含 type: string」来判（那是 enum 项的必要组成），
            // 判据是「enum: 之后紧随列表项」。
            assertThat(block)
                    .as("displayType SHALL 带 enum 列表，否则前端只能硬编码一份会静默过期的副本；"
                            + "实际块=%s", block)
                    .contains("enum:");
            assertThat(block)
                    .as("displayType 的 enum SHALL 后随枚举列表项（而非空的 enum:），实际块=%s", block)
                    .containsPattern("(?s)enum:\\s*\\n\\s+- \\S+");
        }

        @Test
        @DisplayName("displayType 的 enum 取值 SHALL 与枚举类 DisplayAttributeDisplayType 完全一致")
        void displayType_enumMatchesEnumClass() throws Exception {
            // 这是手写 24 个字面量的唯一防线：新增枚举而漏改 @Schema，本用例红。
            java.util.Set<String> expected = new java.util.TreeSet<>();
            for (com.aicode.display.entity.DisplayAttributeDisplayType t
                    : com.aicode.display.entity.DisplayAttributeDisplayType.values()) {
                expected.add(t.name());
            }
            assertThat(expected)
                    .as("枚举类本身 SHALL 非空（否则断言会假通过）")
                    .hasSize(24);

            String block = fieldBlock(contractYaml(), "displayType");
            java.util.Set<String> actual = new java.util.TreeSet<>();
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("^\\s+- (\\S+)$", java.util.regex.Pattern.MULTILINE)
                    .matcher(block);
            while (m.find()) {
                String v = m.group(1);
                // YAML 会给 "=", "!=" 这类值加引号，去掉再比
                if (v.length() >= 2 && v.startsWith("'") && v.endsWith("'")) {
                    v = v.substring(1, v.length() - 1);
                }
                actual.add(v);
            }

            assertThat(actual)
                    .as("@Schema(allowableValues) 手写的字面量 SHALL 与枚举类完全一致"
                            + "（Java 注解只接受编译期常量，无法引用 values()，故只能手写）；"
                            + "缺失=%s / 多余=%s", missing(actual, expected), extra(actual, expected))
                    .isEqualTo(expected);
        }

        private static java.util.Set<String> missing(java.util.Set<String> actual,
                                                     java.util.Set<String> expected) {
            java.util.Set<String> s = new java.util.TreeSet<>(expected);
            s.removeAll(actual);
            return s;
        }

        private static java.util.Set<String> extra(java.util.Set<String> actual,
                                                  java.util.Set<String> expected) {
            java.util.Set<String> s = new java.util.TreeSet<>(actual);
            s.removeAll(expected);
            return s;
        }

        @Test
        @DisplayName("matchType / fieldValidationMode SHALL 同样带非空 enum 列表")
        void matchTypeAndValidationMode_haveEnumInContract() throws Exception {
            String yaml = contractYaml();
            for (String field : java.util.List.of("matchType", "fieldValidationMode")) {
                String block = fieldBlock(yaml, field);
                assertThat(block)
                        .as("%s SHALL 带非空 enum 列表，实际块=%s", field, block)
                        .containsPattern("(?s)enum:\\s*\\n\\s+- \\S+");
            }
        }
    }

    // ==================================================================
    // 场景⑭：账户列表 SHALL 包入 R、返 AccountPublicVO、回填分页字段、入参可缺省
    // （2026-10-18-account-list-security-and-pagination 防护）
    // ==================================================================

    /**
     * 本组守护 {@code /account/list} 修复前的 4 个缺陷。
     *
     * <ol>
     *   <li><b>安全</b>：响应曾返 {@code PageVOAccountVO}，而 {@code AccountVO} 含
     *       {@code password}（{@code Md5.md5()} 无盐存储，可离线爆破）—— 测试库里
     *       就躺着 admin 的真实哈希值。</li>
     *   <li>返回裸 {@code PageVO}，不在 {@code R} 包络内；</li>
     *   <li>{@code new Page<>(curPage, pageSize)} 对 {@code Integer null} 自动拆箱 → <b>NPE</b>
     *       （不带参数访问必然 500）；</li>
     *   <li>三个分页字段从不 set，恒为 0。</li>
     * </ol>
     *
     * <p>另含 2 条同 Controller 的断言：{@code modify} 的 R 包络、{@code delete}
     * 的入参（曾因 {@code @Parameter(hidden = true)} 在契约里<b>完全没有参数</b>）。</p>
     */
    @Nested
    @DisplayName("场景⑭ 账户列表包入 R、返 AccountPublicVO（不含 password）、分页字段回填")
    class AccountListSecurityAndPagination {

        /** `AccountPublicVO` 的 3 字段白名单。 */
        private static final java.util.Set<String> PUBLIC_VO_FIELDS = java.util.Set.of(
                "id", "code", "account"
        );

        @Test
        @DisplayName("data.records[] SHALL 是 AccountPublicVO 形态，SHALL NOT 含 password")
        void accountList_records_neverContainPassword() throws Exception {
            String body = get("/account/list.shtml?curPage=1&pageSize=10", token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("/account/list SHALL 成功（实际=%s）", root.path("info"))
                    .isTrue();

            JsonNode data = root.path("data");
            JsonNode records = data.path("records");
            assertThat(records.isArray())
                    .as("data.records SHALL 是数组，实际=%s", records)
                    .isTrue();
            // 测试库有 admin / aicode 两个账户种子，这里必须有行可校验
            assertThat(records.size())
                    .as("data.records SHALL 非空（db/data.sql 有 2 条账户种子），实际=%s", records)
                    .isGreaterThan(0);

            for (JsonNode record : records) {
                java.util.Set<String> actual = fieldNames(record);
                assertThat(actual)
                        .as("records 元素 SHALL ⊆ AccountPublicVO 3 字段，实际=%s", actual)
                        .isSubsetOf(PUBLIC_VO_FIELDS);
                // 点名断言 —— 凭据泄漏是本组的核心
                assertThat(actual.contains("password"))
                        .as("records 元素 SHALL NOT 含 password（MD5 无盐存储，随响应泄漏即可离线"
                                + "爆破）；实际字段集=%s", actual)
                        .isFalse();
            }
            // 顺带钉住业务字段确实有值 —— 防止为躲泄漏而把响应清空
            assertThat(records.get(0).path("account").asText())
                    .as("records[0].account SHALL 非空（admin / aicode），防止为躲泄漏而空响应")
                    .isNotEmpty();
        }

        @Test
        @DisplayName("响应顶层 SHALL ref RPageVOAccountPublicVO，SHALL NOT 裸 PageVO")
        void accountList_response_isWrappedInR() throws Exception {
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf("/account/list:");
            assertThat(idx).as("契约 SHALL 含 /account/list").isNotNegative();
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));

            assertThat(seg)
                    .as("/account/list 的响应 SHALL 包入 R（裸 PageVO 会让前端 unwrapSuccess 失效）"
                            + "，实际片段=%s", seg)
                    .contains("RPageVOAccountPublicVO");
            assertThat(seg)
                    .as("/account/list SHALL NOT 裸返 PageVOAccountVO，实际片段=%s", seg)
                    .doesNotContain("schemas/PageVOAccountVO\"");
        }

        @Test
        @DisplayName("curPage / pageSize 缺省或非法时 SHALL 回退 1 / 10 而非抛 NPE")
        void accountList_fallsBackToDefaults_insteadOfNpe() throws Exception {
            // 缺陷 3 的实测依据：Page(long, long) 收 long，传 Integer null 会自动拆箱 NPE
            //   → java.lang.NullPointerException: Cannot invoke
            //     "java.lang.Integer.intValue()" because "<local1>" is null
            // 修复前：GET /account/list（不带参数）必然 500
            String body = get("/account/list.shtml?curPage=0&pageSize=-1", token);

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("非法入参 SHALL NOT 让响应失败（仍 R.success），实际=%s", root.path("info"))
                    .isTrue();

            JsonNode data = root.path("data");
            assertThat(data.path("curPage").asInt())
                    .as("curPage=0 SHALL 回退到 1，实际=%s", data.path("curPage"))
                    .isEqualTo(1);
            assertThat(data.path("pageSize").asInt())
                    .as("pageSize=-1 SHALL 回退到 10，实际=%s", data.path("pageSize"))
                    .isEqualTo(10);
        }

        @Test
        @DisplayName("data.curPage / pageSize / totalPage SHALL 回填真实值")
        void accountList_reflectsPaginationParams() throws Exception {
            String body = get("/account/list.shtml?curPage=1&pageSize=10", token);

            JsonNode data = PARSER.readTree(body).path("data");
            assertThat(data.path("curPage").asInt())
                    .as("data.curPage SHALL 回填实际查询参数（期望 1），实际=%s", data.path("curPage"))
                    .isEqualTo(1);
            assertThat(data.path("pageSize").asInt())
                    .as("data.pageSize SHALL 回填实际查询参数（期望 10），实际=%s", data.path("pageSize"))
                    .isEqualTo(10);

            long totalRow = data.path("totalRow").asLong();
            assertThat(totalRow)
                    .as("totalRow SHALL 等于账户总数（db/data.sql 有 2 条），实际=%s", totalRow)
                    .isEqualTo(2);
            assertThat(data.path("totalPage").asInt())
                    .as("data.totalPage SHALL 等于 ceil(totalRow/pageSize)=1，SHALL NOT 为 0")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("PUT /account/modify SHALL ref RBoolean 而非顶层 type: boolean")
        void accountModify_isWrappedInR() throws Exception {
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf("/account/modify:");
            assertThat(idx).as("契约 SHALL 含 /account/modify").isNotNegative();
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));

            assertThat(seg)
                    .as("modify 的响应 SHALL ref RBoolean，实际片段=%s", seg)
                    .contains("RBoolean");
            assertThat(seg)
                    .as("modify SHALL NOT 裸返 boolean（裸出参绕过 R 的 code/info/success），"
                            + "实际片段=%s", seg)
                    .doesNotContain("type: boolean");
        }

        @Test
        @DisplayName("DELETE /account/delete SHALL 在契约中导出参数")
        void accountDelete_exposesParametersInContract() throws Exception {
            // 修复前该形参带 @Parameter(hidden = true) → springdoc 忽略整个形参，
            // 连方法级 @Parameters 块也被盖掉 → 契约里**完全没有参数**，
            // 前端无从构造删除请求。
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf("/account/delete:");
            assertThat(idx).as("契约 SHALL 含 /account/delete").isNotNegative();
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));

            assertThat(seg)
                    .as("delete SHALL 导出 parameters（前端需要它构造删除请求），实际片段=%s", seg)
                    .contains("parameters:");
        }
    }

    // ==================================================================
    // 场景⑮：/project/init SHALL 异步接受并快速返回（2026-10-18-init-async-progress）
    // ==================================================================

    /**
     * 本组守护 {@code POST /project/init} 从「同步执行完毕」改为「立即接受 + ws 推进度」。
     *
     * <p><b>耗时断言是本组的核心</b>：改造前该端点在请求线程里跑完
     * {@code createDatabase}（建库 + 用户的全部建表 SQL）+ {@code parse}（逆解析），
     * 大 SQL 会阻塞数十秒。改异步后必须<b>快速返回</b>。
     * 若有人把 executor 改回同类内部 {@code this.xxx()} 调用（绕过 AOP），
     * {@code @Async} 会静默失效、请求重新同步阻塞 —— 只有耗时断言能抓到。</p>
     *
     * <p>⚠️ 该端点会真的触发建库与解析，故用<b>不存在的项目编码</b>调用：
     * 异步路径下业务失败发生在 HTTP 响应之后（异常逃逸不到调用方），
     * 响应仍应是「已接受」。这同时验证了异步异常处理（AGENTS §6.7）。</p>
     */
    @Nested
    @DisplayName("场景⑮ /project/init 异步接受并快速返回")
    class InitAsyncContract {

        private static final long FAST_RETURN_BUDGET_MS = 2000L;

        @Test
        @DisplayName("POST /project/init SHALL 快速返回 R.success（未同步阻塞到解析结束）")
        void init_returnsQuicklyWithAcceptedSemantics() throws Exception {
            // 用不存在的编码：同步路径下这会抛 Result_Not_Exist → 9006 失败响应；
            // 异步路径下异常发生在响应之后，响应仍应是 success。
            // 因此这条同时验证「异步语义」与「异常不会逃逸到 HTTP 调用方」。
            long start = System.currentTimeMillis();
            String body = post("/project/init.shtml", "code=not-exist-init-contract-test", token);
            long elapsed = System.currentTimeMillis() - start;

            JsonNode root = PARSER.readTree(body);
            assertThat(root.path("success").asBoolean())
                    .as("init SHALL 返回「已接受」（success=true）；若返回失败说明改回了同步执行"
                            + "，实际=%s", root.path("info"))
                    .isTrue();
            assertThat(root.path("code").asText())
                    .as("R.code SHALL 等于 0000，实际=%s", root.path("code"))
                    .isEqualTo("0000");

            assertThat(elapsed)
                    .as("init SHALL 快速返回（预算 %d ms，实测 %d ms）。若超时，说明 @Async 未生效"
                            + " —— 常见原因是 executor 被同类内部 this.xxx() 调用而绕过了 AOP 代理",
                            FAST_RETURN_BUDGET_MS, elapsed)
                    .isLessThan(FAST_RETURN_BUDGET_MS);
        }

        @Test
        @DisplayName("POST /project/init 的响应契约 SHALL 仍是 R<Void>（形状未变）")
        void init_responseContract_unchanged() throws Exception {
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf("/project/init:");
            assertThat(idx).as("契约 SHALL 含 /project/init").isNotNegative();
            String seg = yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));

            assertThat(seg)
                    .as("init 的响应 SHALL 仍是 RVoid（异步化只改语义不改响应形状），"
                            + "实际片段=%s", seg)
                    .contains("RVoid");
            // 入参 code 不得丢 —— 它承载"解析哪个项目"
            assertThat(seg)
                    .as("init SHALL 导出 parameters（code 必需），实际片段=%s", seg)
                    .contains("parameters:");
        }
    }

    // ==================================================================
    // 场景⑯：分页容器 SHALL NOT 携带查询辅助字段（2026-10-18-pagevo-drop-basevo-noise）
    // ==================================================================

    /**
     * 本组守护 {@code PageVO<T>} 不再继承 {@code BaseVO}。
     *
     * <p>{@code BaseVO} 的 4 个字段（{@code createTimeBegin} / {@code createTimeEnd} /
     * {@code updateTimeBegin} / {@code updateTimeEnd}）语义是「<b>入参</b>：按时间范围过滤」。
     * 出参容器继承入参基类，就让<b>每个分页响应</b>的 {@code data} 层带 4 个恒 null 的字段 ——
     * 前端零消费，却固化进了跨语言契约。</p>
     *
     * <p>两条断言：① 数据层（实际调用 {@code /project/list}）
     * ② <b>扫描型</b> —— 遍历契约里每一个 {@code PageVO*} schema。
     * 只写 ① 的话，新增分页端点会漏；② 一次覆盖全部且将来自动纳入。</p>
     */
    @Nested
    @DisplayName("场景⑯ 分页容器不含 BaseVO 查询辅助字段")
    class PageVoQueryFieldNoise {

        /** 4 个查询辅助字段名。 */
        private static final java.util.List<String> QUERY_AUX_FIELDS = java.util.List.of(
                "createTimeBegin", "createTimeEnd", "updateTimeBegin", "updateTimeEnd"
        );

        /** PageVO 的 5 个真实字段 —— 顺带防「过度删除」。 */
        private static final java.util.List<String> PAGE_VO_FIELDS = java.util.List.of(
                "curPage", "pageSize", "totalRow", "totalPage", "records"
        );

        @Test
        @DisplayName("/project/list 的 data SHALL NOT 含 4 个查询辅助字段，且仍含 5 个分页字段")
        void projectList_data_hasNoQueryAuxFields() throws Exception {
            String body = get("/project/list.shtml?curPage=1&pageSize=10", token);

            JsonNode data = PARSER.readTree(body).path("data");
            java.util.Set<String> actual = fieldNames(data);

            for (String aux : QUERY_AUX_FIELDS) {
                assertThat(actual.contains(aux))
                        .as("分页响应的 data SHALL NOT 含查询辅助字段 '%s'（BaseVO 的入参字段，"
                                + "响应里恒为 null）；实际字段集=%s", aux, actual)
                        .isFalse();
            }
            // 防过度删除：真实字段必须还在
            for (String field : PAGE_VO_FIELDS) {
                assertThat(actual.contains(field))
                        .as("data SHALL 仍含分页字段 '%s'（本次只删噪音字段），实际=%s", field, actual)
                        .isTrue();
            }
        }

        @Test
        @DisplayName("契约里 EVERY PageVO* schema SHALL NOT 含查询辅助字段（覆盖所有分页端点）")
        void everyPageVoSchema_hasNoQueryAuxFields() throws Exception {
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

            // components.schemas 下所有以 PageVO 开头的 schema
            java.util.regex.Matcher m = java.util.regex.Pattern
                    .compile("(?m)^    (PageVO[A-Za-z0-9_]*):\s*$")
                    .matcher(yaml);
            // ⚠️ 找「下一个 schema 头」必须用**行首锚定的正则**，不能用
            // indexOf("\n    ")：schema 内部的属性行是 8 空格缩进，而 "\n    "（4 空格）
            // 是它的前缀 —— indexOf 会匹配到第一个属性行，把片段截断成「只有 schema 名」，
            // 于是字段检查全部落空、断言**假通过**（实测踩过；同一陷阱在 fieldBlock() 也踩过一次）。
            java.util.regex.Matcher nextSchema = java.util.regex.Pattern
                    .compile("(?m)^    [A-Za-z][A-Za-z0-9_]*:\\s*$")
                    .matcher(yaml);
            java.util.List<String> offenders = new java.util.ArrayList<>();
            int checked = 0;
            while (m.find()) {
                String name = m.group(1);
                int from = m.end();
                int to = nextSchema.find(from) ? nextSchema.start() : yaml.length();
                String seg = yaml.substring(from, to);
                checked++;
                for (String aux : QUERY_AUX_FIELDS) {
                    if (seg.contains("        " + aux + ":")) {
                        offenders.add(name + "." + aux);
                    }
                }
            }

            assertThat(checked)
                    .as("契约 SHALL 至少含一个 PageVO* schema（扫描断言对 0 个对象会假通过）")
                    .isGreaterThan(0);
            assertThat(offenders)
                    .as("下列分页容器 schema 仍带查询辅助字段 —— PageVO 重新继承了 BaseVO 或"
                            + "某处另建了带该基类的容器")
                    .isEmpty();
        }
    }

    // ==================================================================
    // 场景⑰：框架写侧端点 SHALL 返读投影且契约 SHALL 导出参数
    // （2026-10-18-framework-write-endpoints 防护）
    // ==================================================================

    /**
     * 本组守护 {@code /framework/modify} 与 {@code /framework/updateIsPublic}。
     *
     * <p><b>改造前它们是一对契约里的定时炸弹</b>：出参 {@code R<Frameworks>}（裸实体，
     * 含 {@code password} —— 模板仓库的 Git 口令）+ 形参 {@code @Parameter(hidden = true)}
     * 导致契约里完全无参数。当时无活跃消费者（{@code FrameworkListPage} 只读），
     * 但任何人接框架管理面、用 orval 生成的函数提交，就把 Git 密码写进响应。</p>
     *
     * <p>两条断言：① 凭据 —— 响应 schema SHALL 是 {@code FrameworkSummaryVO}（与
     * {@code /framework/list} 同一个）且不含 {@code password}；② 参数 —— SHALL 导出
     * <b>扁平的</b>业务参数（与运行时一致），而非「一个叫 {@code frameworks} 的对象」。</p>
     */
    @Nested
    @DisplayName("场景⑰ 框架写侧端点返 FrameworkSummaryVO 且契约导出扁平参数")
    class FrameworkWriteEndpoints {

        private static final String FRAMEWORK_CODE = "1882812765213425664";

        private String endpointSeg(String path) throws Exception {
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            int idx = yaml.indexOf(path);
            assertThat(idx).as("契约 SHALL 含 %s 端点", path).isNotNegative();
            return yaml.substring(idx, yaml.indexOf("\n  /", idx + 5));
        }

        @Test
        @DisplayName("modify / updateIsPublic SHALL ref RFrameworkSummaryVO，SHALL NOT 含 password")
        void writeEndpoints_returnSummaryVoNotEntity() throws Exception {
            for (String path : java.util.List.of("/framework/modify:", "/framework/updateIsPublic:")) {
                String seg = endpointSeg(path);

                assertThat(seg)
                        .as("%s 的响应 SHALL ref RFrameworkSummaryVO（与 /framework/list 同一个读投影）"
                                + "，SHALL NOT ref RFrameworks 实体 —— 实体含 password", path)
                        .contains("RFrameworkSummaryVO");
                assertThat(seg)
                        .as("%s SHALL NOT ref 裸实体 RFrameworks，实际片段=%s", path, seg)
                        .doesNotContain("RFrameworks\"");
            }
            // 读投影本身不含 password —— 三处共用同一个 schema，堵一处即堵全部
            String yaml = mockMvc.perform(MockMvcRequestBuilders.get("/v3/api-docs.yaml"))
                    .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
            String voSeg = schemaBlock(yaml, "FrameworkSummaryVO");
            assertThat(voSeg)
                    .as("FrameworkSummaryVO SHALL NOT 含 password（模板仓库 Git 口令），实际=%s", voSeg)
                    .doesNotContain("password:");
        }

        @Test
        @DisplayName("两个端点的 EVERY operation SHALL 导出扁平业务参数")
        void writeEndpoints_exposeFlatParameters() throws Exception {
            // 去掉 @Parameter(hidden=true) 后，springdoc 对**实体形参**会把整个实体导出为
            // 一个 query 参数（`?frameworks=<object>`），而运行时是按字段名绑定
            // （`?code=xxx&isPublic=Y`）—— 契约与运行时不一致，前端照契约实现会失败。
            // 实测 @ModelAttribute 也无效，故改为显式 @RequestParam 形参。
            //
            // ⚠️ **必须逐 operation 检查**：一个 `@RequestMapping(method={PUT,POST})`
            // 会在契约里生成 **put 与 post 两个 operation 共用同一个 path key**；
            // 只扫整个 path 片段的话，任意一个 method 有参数就会让断言通过 ——
            // 实测踩过：给形参加回 hidden 后 PUT 段无参数、POST 段仍从块继承，
            // 断言假通过。故这里逐 method 切片。
            for (String path : java.util.List.of("/framework/modify:", "/framework/updateIsPublic:")) {
                String seg = endpointSeg(path);
                java.util.List<String> verbs = java.util.regex.Pattern
                        .compile("(?m)^    (get|put|post|delete):\\s*$")
                        .matcher(seg).results().map(r -> r.group(1)).toList();
                assertThat(verbs)
                        .as("%s 至少应有一个 operation（否则断言会假通过）", path)
                        .isNotEmpty();

                for (String verb : verbs) {
                    int v = seg.indexOf("\n    " + verb + ":");
                    java.util.regex.Matcher nextOp = java.util.regex.Pattern
                            .compile("(?m)^    (get|put|post|delete):\\s*$")
                            .matcher(seg);
                    int end = nextOp.find(v + 5) ? nextOp.start() : seg.length();
                    String opSeg = seg.substring(v, end);
                    int ps = opSeg.indexOf("parameters:");
                    int pe = opSeg.indexOf("      responses:");
                    java.util.Set<String> names = new java.util.HashSet<>();
                    if (ps > 0 && pe > ps) {
                        java.util.regex.Matcher m = java.util.regex.Pattern
                                .compile("- name: (\\w+)").matcher(opSeg.substring(ps, pe));
                        while (m.find()) {
                            names.add(m.group(1));
                        }
                    }
                    assertThat(names)
                            .as("%s 的 %s operation SHALL 导出扁平业务参数 code，"
                                    + "SHALL NOT 无参数（形参被 @Parameter(hidden=true) 抹掉）"
                                    + "，SHALL NOT 是单个 'frameworks' 对象参数"
                                    + "（springdoc 对实体形参会如此导出，而运行时按字段名绑定）",
                                    path, verb)
                            .contains("code");
                    assertThat(names)
                            .as("%s 的 %s operation SHALL NOT 把整个实体导出为单对象参数", path, verb)
                            .doesNotContain("frameworks");
                }
            }
        }
    }
}
