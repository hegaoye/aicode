package com.aicode.contract;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 导出 OpenAPI 契约到 {@code contracts/openapi/openapi.yaml}（入库的跨语言契约产物）。
 *
 * <p><b>为什么由测试导出而不是构建任务</b>：springdoc-openapi-gradle-plugin 不在 Maven Central
 * （仅在 Gradle Plugin Portal），引入它等于新增插件源依赖；而仓库已有 {@code measurement/} 脚本
 * 用「启停应用 + curl」的先例，但那条路需要占用端口、约 20 秒启动，且脱离测试生命周期。
 * 复用既有的 {@code @SpringBootTest} 上下文是唯一既无额外依赖、又能随测试自动执行的路径。
 *
 * <p><b>为什么用行级剥离而不是 YAML 反序列化再回写</b>：本项目运行时是 Jackson 3
 * （{@code tools.jackson}），而测试类路径上的 {@code jackson-dataformat-yaml} 是 Jackson 2
 * （经 snakeyaml 传递）。若用后者做「解析 → 移除节点 → 重新序列化」，整个文件会被按
 * Jackson 2 的排版规则重写，契约的 diff 将被格式噪声淹没 —— 而这个文件存在的全部意义
 * 就是让接口变更在评审中一眼可见。行级剥离保留了 springdoc 自身的排版，diff 只反映真实变化。
 *
 * <p><b>漂移守卫</b>：本测试只负责「刷新」。由于文件入库，代码改了而契约未提交会体现为
 * {@code git diff} 非空 —— {@code :aicode-web:verifyApi} 据此判定过期。
 *
 * @see openspec/specs/api-contract-artifact/spec.md
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
class OpenApiExportIT {

    /** 入库的契约文件路径（仓库根下的 contracts/openapi/）。 */
    static final Path CONTRACT = Paths.get("..", "contracts", "openapi", "openapi.yaml")
            .toAbsolutePath().normalize();

    @Autowired
    private WebApplicationContext wac;

    /**
     * MOCK web 环境没有 {@code jakarta.websocket.server.ServerContainer}，
     * {@code WebSocketConfig} 注册的 {@code ServerEndpointExporter} 会因此创建失败。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    @Test
    @DisplayName("导出 OpenAPI 契约到 contracts/openapi/openapi.yaml，并剥离与运行机器相关的 servers 节点")
    void exportOpenApiContract_excludingServers() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        String raw = mockMvc.perform(get("/v3/api-docs.yaml"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);

        assertThat(raw).as("应以 openapi: 开头").startsWith("openapi:");
        assertThat(raw).as("应含 paths 节点").contains("\npaths:");

        String normalized = stripServersBlock(raw);

        assertThat(normalized)
                .as("servers 的 URL 由本次请求的 host/port 生成，入库会产生逐端口的无意义 diff")
                .doesNotContain("servers:");
        assertThat(normalized)
                .as("不应残留任何 127.0.0.1 之类的运行时取值")
                .doesNotContain("127.0.0.1");
        assertThat(normalized)
                .as("剥离不得破坏其余内容")
                .contains("openapi:", "\npaths:", "\ninfo:", "\ncomponents:");

        Files.createDirectories(CONTRACT.getParent());
        Files.writeString(CONTRACT, normalized, StandardCharsets.UTF_8);

        assertThat(CONTRACT).as("契约文件 SHALL 已入库").exists();
    }

    /**
     * 剥离顶层的 {@code servers:} 块。
     *
     * <p>springdoc 输出的形态（实测）：
     * <pre>
     * servers:
     * - url: http://127.0.0.1:8090
     *   description: Generated server url
     * tags:
     * </pre>
     * 列表项以 {@code - } 顶格书写，因此块的结束条件是：遇到下一个
     * <b>顶格、非空白、非 {@code -} 开头</b>的行（即下一个顶层键）。
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
                // 下一个顶层键：顶格、不以 '-' 开头
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