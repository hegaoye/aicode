package com.aicode.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * M4 切运行时集成测试 —— 守护「React 前端接管 `/`，后端 API 仍走 Spring MVC」。
 *
 * <p>详见 {@code openspec/specs/api-contract/spec.md} 的 Requirement
 * 「M4 切运行时 React 入口优先 Angular 静态产物」。</p>
 *
 * <h3>为什么用 RANDOM_PORT + JDK HttpClient 而不是 MockMvc</h3>
 * <p>MockMvc（MOCK 环境）下 Spring Boot 用普通 classloader，<b>读不到
 * {@code BOOT-INF/lib/aicode-web.jar} 这层嵌套 jar 里的 {@code static/**}</b> ——
 * 实测 {@code GET /} 返回 {@code Content-Type=null}、body 为空。真实运行时
 * （{@code bootRun}）用 {@code LaunchedURLClassLoader} 能正常读到。因此必须起真实 HTTP 端口。</p>
 *
 * <p>也未用 {@code TestRestTemplate}：Spring Boot 4.1.1 已移除该类（实测
 * {@code package org.springframework.boot.test.web.client does not exist}），
 * 改用 JDK 内置 {@link HttpClient}（跨 Boot 版本稳定、无额外依赖）。</p>
 *
 * <h3>4 个关键不变量</h3>
 * <ol>
 *   <li>{@code GET /} 返 React HTML（含 {@code id="root"}），不返 Angular 老产物 hash</li>
 *   <li>{@code GET /project/list.shtml} 仍返 R 包络 JSON（不被 React 入口拦截）</li>
 *   <li>{@code GET /login}（无 hash）返 400 JSON —— M4 D5 决策的<b>预期行为</b>：
 *       前端页面路由走 hash（{@code /#/login}），服务端不做 SPA fallback</li>
 *   <li>{@code GET /assets/*} 由 Spring Boot 静态资源提供</li>
 * </ol>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class M4CutoverIntegrationTest {

    /** Angular 7 老产物的一个标志性 hash 文件名 —— M4 后 SHALL 永不出现。 */
    private static final String ANGULAR_HASH_MARKER = "10.460f310dc07dcf545995.js";

    @LocalServerPort
    private int port;

    /**
     * 打桩 WebSocket 端点注册器 —— test profile 下无真实容器时
     * {@code WebSocketConfig#serverEndpointExporter} 创建失败（与 ApiContractIT 同因）。
     */
    @MockitoBean
    private org.springframework.web.socket.server.standard.ServerEndpointExporter serverEndpointExporter;

    /** JDK 内置 HttpClient —— Boot 4.1.1 已移除 TestRestTemplate，用标准库更稳。 */
    private final HttpClient http = HttpClient.newHttpClient();

    private HttpResponse<String> get(String path) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + path))
                .header("x-requested-with", "XMLHttpRequest")
                .GET()
                .build();
        return http.send(req, HttpResponse.BodyHandlers.ofString());
    }

    @Test
    @DisplayName("不变量①：GET / 返 React HTML（id=root），不返 Angular 老产物")
    void root_servesReactEntry_notAngular() throws Exception {
        HttpResponse<String> resp = get("/");

        assertThat(resp.statusCode()).isEqualTo(200);
        String contentType = resp.headers().firstValue("Content-Type").orElse(null);
        assertThat(contentType)
                .as("/ 的 Content-Type SHALL 为 text/html，实际=%s", contentType)
                .contains("text/html");

        String body = resp.body();
        assertThat(body)
                .as("/ 的 body SHALL 含 React 入口标记 id=\"root\"（aicode-web Vite 产物）")
                .contains("id=\"root\"");
        assertThat(body)
                .as("/ 的 body SHALL NOT 含 Angular 老产物 hash 标记（%s）", ANGULAR_HASH_MARKER)
                .doesNotContain(ANGULAR_HASH_MARKER);
    }

    @Test
    @DisplayName("不变量②：GET /project/list.shtml 仍由 Spring MVC 处理，返 R 包络 JSON")
    void apiWithShtmlSuffix_stillServedBySpringMvc() throws Exception {
        HttpResponse<String> resp = get("/project/list.shtml?token=contract-test&curPage=1&pageSize=5");

        assertThat(resp.statusCode()).isEqualTo(200);
        String contentType = resp.headers().firstValue("Content-Type").orElse(null);
        assertThat(contentType)
                .as(".shtml API 路径的 Content-Type SHALL 为 application/json（不被 React 入口拦截），实际=%s",
                        contentType)
                .contains("application/json");

        String body = resp.body();
        assertThat(body)
                .as("body SHALL 是 R 包络 JSON（含 code 键），实际=%s", body)
                .contains("\"code\"");
    }

    @Test
    @DisplayName("不变量③：GET /login（无 hash）返 400 JSON —— M4 D5 决策的预期行为")
    void pageRouteWithoutHash_returns400_notReactEntry() throws Exception {
        // M4 D5 决策：不做 SPA fallback。前端页面路由走 hash 段（/#/login），
        // 服务端只把 `/` 当入口。因此无 hash 的 /login 落到 static resource handler
        // 抛 NoResourceFoundException → ExceptionHandle 转 R.failed(9999)。
        //
        // 本断言把设计决策钉成可执行事实：若未来有人加 SPA fallback，本用例会变红。
        HttpResponse<String> resp = get("/login");

        assertThat(resp.statusCode())
                .as("无 hash 的页面路径 SHALL 返 400（不做 SPA fallback），实际=%s", resp.statusCode())
                .isEqualTo(400);

        String body = resp.body();
        assertThat(body)
                .as("SHALL 返 R 包络错误 JSON（含 code 键）")
                .contains("\"code\"");
        assertThat(body)
                .as("SHALL NOT 返 React 入口 HTML")
                .doesNotContain("id=\"root\"");
    }

    @Test
    @DisplayName("不变量④：GET /assets/* 由 Spring Boot 静态资源提供")
    void staticAssets_servedBySpringBoot() throws Exception {
        String rootBody = get("/").body();

        java.util.regex.Matcher m = java.util.regex.Pattern
                .compile("/assets/[A-Za-z0-9._-]+\\.js")
                .matcher(rootBody == null ? "" : rootBody);
        assertThat(m.find())
                .as("/ 的 body SHALL 引用至少一个 /assets/*.js 资源，实际=%s", rootBody)
                .isTrue();

        HttpResponse<String> assetResp = get(m.group());
        assertThat(assetResp.statusCode()).isEqualTo(200);

        String jsBody = assetResp.body();
        assertThat(jsBody)
                .as("assets JS 产物 SHALL 非空（React bundle 已打包进 aicode-web jar）")
                .isNotNull()
                .isNotEmpty();
        // React bundle 应含 root 挂载标记（验证是 React 而不是别的库）
        assertThat(jsBody)
                .as("assets JS SHALL 含 React 的 root 挂载标记（验证产物确为 React）")
                .contains("root");
    }
}