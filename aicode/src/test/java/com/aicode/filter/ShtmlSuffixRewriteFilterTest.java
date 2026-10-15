package com.aicode.filter;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * {@link ShtmlSuffixRewriteFilter} 的路径重写逻辑单元测试。
 *
 * <p>刻意<b>不启动 Spring 容器</b>：被测的 {@code shouldRewrite} 是纯函数式判定，
 * 与容器、过滤器链、DispatcherServlet 均无关。覆盖四类输入：带后缀、无后缀、
 * 前缀外、静态资源。</p>
 *
 * <p>白名单与 {@code FrontendEndpointCatalog.REGISTERED_PREFIXES} 的一致性由
 * {@link #apiPrefixes_matchFrontendEndpointCatalog()} 交叉校验 —— 两处若不同步，
 * 前端调用的端点会漏掉后缀兼容。</p>
 */
class ShtmlSuffixRewriteFilterTest {

    // ---------- shouldRewrite：带后缀且在前缀白名单内 → true ----------

    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {
            "/project/list.shtml",
            "/project/job/execute.shtml",
            "/project/relationship/listMapFieldColumn.shtml",
            "/framework/list.shtml",
            "/framework/updateIsPublic.shtml",
            "/displayAttribute/list.shtml",
            "/displayAttribute/save.shtml",
            "/logs/load.shtml",
            "/login/signin.shtml",
            "/login/reg.shtml",
            "/account/list.shtml"
    })
    @DisplayName("API 前缀 + .shtml 后缀 → 应重写")
    void apiPrefixWithShtmlSuffix_isRewritten(String path) {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(path))
                .as("%s 落在 API 前缀白名单内且带 .shtml 后缀，应被重写", path)
                .isTrue();
    }

    // ---------- 无后缀 → false（原生路由，不该动） ----------

    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {
            "/project/list",
            "/framework/list",
            "/login/signin",
            "/logs/load",
            "/",
            "/index.html"
    })
    @DisplayName("不以 .shtml 结尾 → 不重写")
    void pathWithoutShtmlSuffix_isNotRewritten(String path) {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(path))
                .as("%s 无 .shtml 后缀，应原样放行", path)
                .isFalse();
    }

    // ---------- 前缀外即使带后缀 → false（关键：保护静态资源） ----------

    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {
            "/main/buildPro/proSteps/proInfo.shtml",
            "/sandbox/foo.shtml",
            "/templates/index.shtml",
            "/workspace/a/b.shtml",
            "/repository/x.shtml"
    })
    @DisplayName("前缀之外即使带 .shtml 后缀 → 不重写")
    void pathOutsideApiPrefix_isNotRewritten(String path) {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(path))
                .as("%s 不在 API 前缀白名单内，应原样放行（可能是前端路由）", path)
                .isFalse();
    }

    // ---------- 静态资源：点号结尾但绝不重写 ----------

    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {
            "/assets/i18n/zh-CN.shtml",
            "/main.js",
            "/styles.css",
            "/favicon.ico",
            "/assets/monaco/editor.min.js",
            "/assets/fonts/icon.woff2"
    })
    @DisplayName("静态资源 → 绝不重写")
    void staticResources_areNeverRewritten(String path) {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(path))
                .as("静态资源 %s SHALL NOT 被后缀兼容层改写，否则破坏 addResourceHandler(\"/**\") 的解析", path)
                .isFalse();
    }

    @Test
    @DisplayName("null / 空路径 → 不重写且不抛异常")
    void nullAndEmptyPath_isSafe() {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(null)).isFalse();
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("")).isFalse();
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("/")).isFalse();
    }

    // ---------- 边界：前缀必须出现在路径起始位置 ----------

    @Test
    @DisplayName("前缀出现在路径中段而非起始 → 不重写")
    void prefixMustBeAtPathStart() {
        // 若用 contains() 而非 startsWith()，前端路由 /main/buildPro/proSteps/project/list.shtml
        // 会被误改写，从而破坏 Angular SPA 路由
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("/main/buildPro/proSteps/project/list.shtml"))
                .as("startsWith 语义：只有以 /project 开头的路径才重写")
                .isFalse();
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("/myproject/list.shtml"))
                .as("/myproject 以 /project 的字符串开头但非路径前缀，不应命中")
                .isFalse();
    }

    @Test
    @DisplayName("仅匹配完整前缀段：/projects 不应命中 /project")
    void prefixMustMatchWholeSegment() {
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("/projects/list.shtml"))
                .as("/projects 与白名单 /project 不是同一段，不应重写")
                .isFalse();
        assertThat(ShtmlSuffixRewriteFilter.shouldRewrite("/project/list.shtml"))
                .as("控制组：/project 应命中")
                .isTrue();
    }

    // ---------- 与前端端点清单的交叉校验 ----------

    @Test
    @DisplayName("白名单与前端端点清单的已注册前缀保持一致")
    void apiPrefixes_matchFrontendEndpointCatalog() {
        assertThat(ShtmlSuffixRewriteFilter.API_PREFIXES)
                .as("Filter 的前缀白名单 SHALL 与 FrontendEndpointCatalog.REGISTERED_PREFIXES 一致，"
                        + "否则前端调用的端点会漏掉 .shtml 兼容")
                .containsExactlyInAnyOrderElementsOf(
                        com.aicode.contract.FrontendEndpointCatalog.REGISTERED_PREFIXES);
    }

    @Test
    @DisplayName("白名单覆盖前端清单中的全部端点")
    void allFrontendEndpoints_areCoveredByWhitelist() {
        List<String> reachable = com.aicode.contract.FrontendEndpointCatalog.REACHABLE;
        assertThat(reachable).isNotEmpty();
        for (String endpoint : reachable) {
            assertThat(ShtmlSuffixRewriteFilter.shouldRewrite(endpoint + ShtmlSuffixRewriteFilter.SHTML_SUFFIX))
                    .as("前端端点 %s SHALL 被后缀兼容层覆盖", endpoint)
                    .isTrue();
        }
    }

    // ---------- RequestWrapper：只改 URI，不动其他 ----------

    @Test
    @DisplayName("Wrapper 剥离 getRequestURI / getServletPath，但保留其余方法")
    void wrapper_stripsUriOnly() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/project/list.shtml");
        request.setContextPath("");
        request.setServletPath("/project/list.shtml");

        ShtmlSuffixRewriteFilter.ShtmlStrippingRequestWrapper wrapper =
                new ShtmlSuffixRewriteFilter.ShtmlStrippingRequestWrapper(request, "");

        assertThat(wrapper.getRequestURI())
                .as("路由匹配读 getRequestURI，须剥离后缀")
                .isEqualTo("/project/list");
        assertThat(wrapper.getServletPath())
                .as("路由匹配读 getServletPath，须剥离后缀")
                .isEqualTo("/project/list");
        assertThat(wrapper.getContextPath())
                .as("contextPath SHALL 保持原样")
                .isEqualTo(request.getContextPath());
        assertThat(wrapper.getRequestURL().toString())
                .as("requestURL 用于日志与 URL 重写类组件，SHALL 保持原样")
                .isEqualTo(request.getRequestURL().toString());
        assertThat(wrapper.getQueryString())
                .as("查询串 SHALL 保持原样")
                .isEqualTo(request.getQueryString());
    }

    @Test
    @DisplayName("Wrapper 在非 context-root 部署下正确处理前缀")
    void wrapper_handlesContextPath() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/aicode/project/list.shtml");
        request.setContextPath("/aicode");
        request.setServletPath("/project/list.shtml");

        ShtmlSuffixRewriteFilter.ShtmlStrippingRequestWrapper wrapper =
                new ShtmlSuffixRewriteFilter.ShtmlStrippingRequestWrapper(request, "/aicode");

        assertThat(wrapper.getRequestURI())
                .as("getRequestURI 含 contextPath，但后缀仍须剥离（DispatcherServlet 会自行减去 contextPath）")
                .isEqualTo("/aicode/project/list");
        assertThat(wrapper.getServletPath())
                .as("getServletPath 不含 contextPath")
                .isEqualTo("/project/list");
    }
}
