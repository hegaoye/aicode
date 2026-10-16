package com.aicode.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 导出 OpenAPI 契约到 {@code contracts/openapi/openapi.yaml}（入库的跨语言契约产物）。
 *
 * <p><b>为什么由测试导出而不是构建任务</b>：springdoc-openapi-gradle-plugin 不在 Maven Central
 * （仅在 Gradle Plugin Portal），引入它等于新增插件源依赖；仓库已有 {@code measurement/} 脚本
 * 用「启停应用 + curl」的先例，但那条路要占端口、约 20 秒启动，且脱离测试生命周期。
 *
 * <p><b>为什么用行级处理而不是 YAML 反序列化再回写</b>：本项目运行时是 Jackson 3
 * （{@code tools.jackson}），而测试类路径上的 {@code jackson-dataformat-yaml} 是 Jackson 2
 * （经 snakeyaml 传递）。用后者「解析 → 改 → 重新序列化」会把整个文件按 Jackson 2 的排版重写，
 * 契约 diff 被格式噪声淹没 —— 而这个文件存在的全部意义就是让接口变更在评审中一眼可见。
 *
 * <p><b>漂移守卫</b>：本测试只负责刷新。文件入库后，「当前内容」与「提交态」的差异
 * 即等价于「代码改了但契约未提交」，由 {@code :aicode-web:verifyApi} 据此判定过期。
 *
 * @see openspec/specs/api-contract-artifact/spec.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class OpenApiExportIT {

    /** 入库的契约文件路径（仓库根下的 contracts/openapi/）。 */
    private static final Path CONTRACT =
            Paths.get("..", "contracts", "openapi", "openapi.yaml").toAbsolutePath().normalize();

    @Autowired
    private WebApplicationContext wac;

    /**
     * MOCK web 环境没有 {@code jakarta.websocket.server.ServerContainer}，
     * {@code WebSocketConfig} 注册的 {@code ServerEndpointExporter} 会因此创建失败。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @Test
    @DisplayName("导出 OpenAPI 契约，剥离 servers 块与无 schema 的装饰性参数条目")
    void exportOpenApiContract_normalized() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        String raw = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(raw).as("应以 openapi: 开头").startsWith("openapi:");
        assertThat(raw).as("应含 paths 节点").contains("\npaths:");

        String normalized = stripSchemaLessParameters(stripServersBlock(raw));

        assertThat(normalized).as("servers 的 URL 由本次请求的 host/port 生成，入库会产生逐端口的无意义 diff")
                .doesNotContain("servers:").doesNotContain("127.0.0.1");
        assertThat(normalized).as("剥离不得破坏其余内容")
                .contains("openapi:", "\ninfo:", "\ntags:", "\npaths:", "\ncomponents:");

        // 自洽校验：parameters: 作用域内不应再有无 schema 的条目
        assertThat(countSchemaLessParameters(normalized))
                .as("剥离后 parameters: 作用域内不应残留无 schema 的条目 —— orval 会因 OpenAPI 违规拒绝生成")
                .isZero();
        // 反向校验：剥离必须是有选择的，操作级 tags: 同为 6 空格 - name:，不得被误删
        assertThat(normalized)
                .as("操作级 tags 同样是 '      - name:' 形态，剥离必须限定在 parameters: 作用域内")
                .contains("\n      - name: ");

        Files.createDirectories(CONTRACT.getParent());
        Files.writeString(CONTRACT, normalized, StandardCharsets.UTF_8);
        assertThat(CONTRACT).exists();
    }

    // ================================================================
    // 规范化
    // ================================================================

    /** 上一级键：缩进 <= 4 且以非空白开头。 */
    private static final java.util.regex.Pattern PARENT_KEY =
            java.util.regex.Pattern.compile("^ {0,4}\\S.*");

    /**
     * 与 {@code parameters:} 同层级（6 空格）的键 —— parameters 列表的结束标志。
     * <p>必须单独判定：{@code responses:} 同为 6 空格，若不识别就会把响应体一并吞进
     * 参数条目，从而把响应里的 {@code schema:} 误判为该参数拥有 schema。
     * <p>负向先行断言 {@code (?!-)} 用于排除 6 空格的列表项本身（{@code - name: xxx}）——
     * 否则 {@code -} 也算非空白字符，会让作用域在处理到条目之前就退出。
     */
    private static final java.util.regex.Pattern SIBLING_KEY =
            java.util.regex.Pattern.compile("^ {6}(?!-)\\S.*");

    /** {@code parameters:} 列表的起始行（6 空格）。 */
    private static final String PARAMETERS_KEY = "      parameters:";

    /** 参数条目起始（6 空格 + "- name: "）—— 与操作级 tags 同形态，故必须靠作用域区分。 */
    private static final String PARAM_ITEM = "      - name: ";

    private static final String ITEM_START = "      - ";

    /**
     * 剥离 {@code parameters:} 作用域内<b>缺少 {@code schema} 节点</b>的参数条目。
     *
     * <p><b>为什么必须剥离</b>：OpenAPI 规范要求每个 parameter 必须带 {@code schema} 或
     * {@code content}，二者皆无即违规；实测导出的 163 个参数中 <b>119 个</b>违规，
     * orval 因此直接拒绝生成。
     *
     * <p><b>更关键的是它们语义错误</b>。例如
     * {@code public R<Frameworks> updateIsPublic(@Parameter(hidden = true) Frameworks frameworks)}
     * 的方法签名里<b>根本没有</b> {@code code} / {@code isPublic} 参数 —— 后端是从
     * {@code Frameworks} 请求体里读的，注解却把它们声明成 query 参数。契约若保留它们，
     * 生成出的前端客户端会发送后端从不读取的 query 参数。
     *
     * <p>因此剥离它们不是「掩盖问题」，而是<b>移除错误信息</b>：剥离后契约描述的才是
     * 后端真实的输入契约（springdoc 从真实方法签名推导出的 44 个参数原样保留）。
     *
     * <p>待修：这些 {@code @Parameters} 块应逐个复核 —— 删除与签名不符者、
     * 为确有其事者补 {@code schema = @Schema(type = ...)}。
     */
    private static String stripSchemaLessParameters(String yaml) {
        String[] lines = yaml.split("\n", -1);
        List<String> kept = new ArrayList<>(lines.length);
        int dropped = 0;
        boolean inParameters = false;

        for (int i = 0; i < lines.length; ) {
            String line = lines[i];

            if (line.equals(PARAMETERS_KEY)) {
                inParameters = true;
                kept.add(line);
                i++;
                continue;
            }
            if (inParameters && (PARENT_KEY.matcher(line).matches() || SIBLING_KEY.matcher(line).matches())) {
                inParameters = false;   // parameters: 列表结束（同级键或更高层）
            }

            if (inParameters && line.startsWith(PARAM_ITEM)) {
                int j = i + 1;
                boolean hasSchema = false;
                while (j < lines.length
                        && !lines[j].startsWith(ITEM_START)
                        && !SIBLING_KEY.matcher(lines[j]).matches()
                        && !PARENT_KEY.matcher(lines[j]).matches()) {
                    String t = lines[j].trim();
                    if (t.equals("schema:") || t.startsWith("content:")) {
                        hasSchema = true;
                    }
                    j++;
                }
                if (!hasSchema) {
                    dropped++;
                    i = j;
                    continue;
                }
            }

            kept.add(line);
            i++;
        }

        // 剥空后的 parameters: 会被 YAML 解析为 null，违反「必须是数组」。
        // 因此连键一起删除 —— 全被剥空意味着该操作没有任何真实入参。
        String joined = String.join("\n", kept);
        String withoutEmptyLists = joined.replaceAll(
                "(?m)^" + Pattern.quote(PARAMETERS_KEY) + "\n(?!" + Pattern.quote(ITEM_START) + ")",
                "");
        System.out.println("[OpenApiExportIT] 剥离无 schema 的装饰性参数条目: " + dropped
                + " 条；顺带移除被剥空的 parameters: 节点");
        return withoutEmptyLists;
    }

    /** 统计 {@code parameters:} 作用域内无 schema 的条目数 —— 用于断言自洽。 */
    private static int countSchemaLessParameters(String yaml) {
        String[] lines = yaml.split("\n", -1);
        int count = 0;
        boolean inParameters = false;
        for (int i = 0; i < lines.length; ) {
            String line = lines[i];
            if (line.equals(PARAMETERS_KEY)) {
                inParameters = true;
                i++;
                continue;
            }
            if (inParameters && (PARENT_KEY.matcher(line).matches() || SIBLING_KEY.matcher(line).matches())) {
                inParameters = false;
            }
            if (inParameters && line.startsWith(PARAM_ITEM)) {
                int j = i + 1;
                boolean hasSchema = false;
                while (j < lines.length
                        && !lines[j].startsWith(ITEM_START)
                        && !SIBLING_KEY.matcher(lines[j]).matches()
                        && !PARENT_KEY.matcher(lines[j]).matches()) {
                    String t = lines[j].trim();
                    if (t.equals("schema:") || t.startsWith("content:")) {
                        hasSchema = true;
                    }
                    j++;
                }
                if (!hasSchema) {
                    count++;
                    i = j;
                    continue;
                }
            }
            i++;
        }
        return count;
    }

    /**
     * 剥离顶层的 {@code servers:} 块。
     *
     * <p>springdoc 输出的形态（实测）：{@code servers:} 后是顶格的 {@code - url: ...}，
     * 因此块的结束条件是遇到下一个<b>顶格、非空白、非 {@code -} 开头</b>的行（即下一个顶层键）。
     */
    private static String stripServersBlock(String yaml) {
        String[] lines = yaml.split("\n", -1);
        StringBuilder out = new StringBuilder(yaml.length());
        boolean inServers = false;
        for (String line : lines) {
            if (line.startsWith("servers:")) {
                inServers = true;
                continue;
            }
            if (inServers) {
                if (!line.isEmpty() && !Character.isWhitespace(line.charAt(0)) && !line.startsWith("-")) {
                    inServers = false;
                } else {
                    continue;
                }
            }
            out.append(line).append('\n');
        }
        return out.toString();
    }
}