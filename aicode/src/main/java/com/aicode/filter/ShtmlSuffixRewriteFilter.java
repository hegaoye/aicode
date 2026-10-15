package com.aicode.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * {@code .shtml} 后缀兼容层。
 *
 * <h3>为什么需要它</h3>
 * 前端是 Angular 7.0.4 + jQuery 的<b>冻结打包产物</b>，其全局 {@code $.ajaxPrefilter}
 * 会无条件为每个 API 请求 URL 追加 {@code .shtml} 后缀：
 *
 * <pre>
 *   t.hasOwnProperty("shtml") || (t.url += ".shtml")
 * </pre>
 *
 * <p>WebSocket 端点同样是 {@code ws://host/websocket.shtml}。
 * Spring Framework 7 已移除 {@code PathMatchConfigurer#setUseSuffixPatternMatch}
 * 与 {@code setUseTrailingSlashMatch}，后缀匹配不再是框架的默认行为 ——
 * 若不做兼容，前端 100% 的接口调用都会 404，且报错只说 "Not Found"，
 * 不会提示「你删错了配置」。</p>
 *
 * <h3>本过滤器做什么</h3>
 * 对<b>已注册的 API 前缀</b>，把以 {@code .shtml} 结尾的请求路径重写为无后缀路径，
 * 再交给 {@code DispatcherServlet}。因此后端 45 个端点的 {@code @RequestMapping}
 * 一行都不用改，前端也完全无感。
 *
 * <h3>为什么限定前缀白名单</h3>
 * 静态资源中有大量以点号结尾的文件（{@code *.css} / {@code *.js} / {@code *.svg} /
 * {@code *.woff2}）。若无差别重写，会破坏 {@code addResourceHandler("/**")} 的解析。
 * 白名单即 {@code FrontendEndpointCatalog} 中登记的六个已注册一级路由前缀。
 *
 * <h3>与拦截器的关系</h3>
 * 重写发生在 {@code DispatcherServlet} 之前，因此 {@code LoginInterceptor} 的
 * {@code excludePathPatterns} 仍在<b>重写后的路径</b>上匹配 —— {@code /login/**}
 * 的排除规则依然生效。
 *
 * @see ContextConfiguration#addInterceptors
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class ShtmlSuffixRewriteFilter extends OncePerRequestFilter {

    /** 后缀常量 */
    static final String SHTML_SUFFIX = ".shtml";

    /**
     * 已注册的 API 一级路由前缀白名单。
     *
     * <p>与 {@code FrontendEndpointCatalog.REGISTERED_PREFIXES} 保持一致
     * （在测试 {@code ShtmlSuffixRewriteFilterTest} 中有交叉校验断言）。</p>
     */
    static final List<String> API_PREFIXES = List.of(
            "/project",
            "/framework",
            "/displayAttribute",
            "/logs",
            "/login",
            "/account");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        String lookup = contextPath.isEmpty() ? path : path.substring(contextPath.length());

        if (shouldRewrite(lookup)) {
            filterChain.doFilter(new ShtmlStrippingRequestWrapper(request, contextPath), response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    /**
     * 判定是否需要重写：路径以 {@code .shtml} 结尾，且落在 API 前缀白名单内。
     *
     * <p><b>按完整路径段匹配</b>，而非简单 {@code startsWith}：后者会把
     * {@code /projects/list.shtml} 误判为命中 {@code /project}。
     * 判据是「前缀之后紧跟 {@code /} 或路径结束」。</p>
     *
     * <p>纯函数式，无副作用，便于单元测试。</p>
     */
    static boolean shouldRewrite(String pathWithinContext) {
        if (pathWithinContext == null || !pathWithinContext.endsWith(SHTML_SUFFIX)) {
            return false;
        }
        return API_PREFIXES.stream().anyMatch(prefix -> startsWithSegment(pathWithinContext, prefix));
    }

    /**
     * 路径是否以 {@code prefix} 这个<b>完整段</b>开头。
     *
     * <p>{@code /project} 匹配 {@code /project} 与 {@code /project/x}，
     * 但不匹配 {@code /projects}（段名不同）也不匹配 {@code /myproject}（非起始）。</p>
     */
    private static boolean startsWithSegment(String path, String prefix) {
        if (!path.startsWith(prefix)) {
            return false;
        }
        return path.length() == prefix.length() || path.charAt(prefix.length()) == '/';
    }

    /**
     * 把 {@code getRequestURI()} / {@code getServletPath()} 中的 {@code .shtml} 后缀剥掉。
     *
     * <p>只改这两个方法：Spring MVC 的路由匹配读的是它们，而
     * {@code getContextPath()} / {@code getRequestURL()} 等保持原样，
     * 以免影响日志与 URL 重写类组件。</p>
     */
    static final class ShtmlStrippingRequestWrapper extends HttpServletRequestWrapper {

        private final String strippedUri;
        private final String strippedServletPath;

        ShtmlStrippingRequestWrapper(HttpServletRequest request, String contextPath) {
            super(request);
            this.strippedUri = strip(request.getRequestURI(), contextPath);
            this.strippedServletPath = strip(request.getServletPath(), contextPath);
        }

        private static String strip(String value, String contextPath) {
            if (value == null || !value.endsWith(SHTML_SUFFIX)) {
                return value;
            }
            return value.substring(0, value.length() - SHTML_SUFFIX.length());
        }

        @Override
        public String getRequestURI() {
            return strippedUri;
        }

        @Override
        public String getServletPath() {
            return strippedServletPath;
        }
    }
}
