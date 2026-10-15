package com.aicode.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.*;

/**
 * Created by  on 2018/6/23.
 */
@Component
public class ContextConfiguration implements WebMvcConfigurer {

    @Autowired
    private ContextInterceptor contextInterceptor;
    @Autowired
    private LoginInterceptor loginInterceptor;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)
                .setUseTrailingSlashMatch(true);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //全局链路追踪：注入 traceId 到 MDC
        registry.addInterceptor(contextInterceptor)
                .addPathPatterns("/**");

        //登录拦截：写操作 / 受保护资源要求 token
        //token 兼容：URL ?token=xxx（兼容现有 .opencode skill 与老接口） + Authorization: Bearer xxx（推荐）
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        //登录（包括 signin.shtml / signin.html 等后缀形式，避免前端 .shtml 调用被拦截）
                        "/login/**",
                        //H2 控制台
                        "/h2",
                        "/h2/**",
                        //Actuator
                        "/actuator/**",
                        //API 文档
                        "/doc.html",
                        "/webjars/**",
                        "/v3/api-docs",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/swagger-ui/**",
                        //静态资源
                        "/",
                        "/index.html",
                        "/favicon.ico",
                        "/static/**",
                        "/assets/**",         // Angular 资源（icons/img/css/js/i18n/fonts/monaco 等多段子目录）
                        "/*",                 // 根目录其他文件（3rdpartylicenses.txt 等）
                        // ⚠️ 下面这批通配规则（"/**/*"、"/*.html"、"/*.js"、"/*.css"、"/*.png" …）
                        //    原本只为放行静态资源，但它们同样匹配带 .shtml 后缀的 API 路径
                        //    （如 /framework/list.shtml），导致 LoginInterceptor 对前端的
                        //    所有请求都不做 token 校验。
                        //    该行为已由 ApiContractIT 记录并**经评估确认不修复**
                        //    （收紧排除规则属独立的安全加固工作，不在平台升级范围内）。
                        //    见 openspec/specs/api-contract/spec.md 与
                        //    ApiContractIT#unauthorized_returns9007AsJson 的 Javadoc。
                        "/**/*",              // 任意深度任意文件
                        "/*.html",
                        "/*.js",
                        "/*.css",
                        "/*.png",
                        "/*.jpg",
                        "/*.jpeg",
                        "/*.gif",
                        "/*.svg",
                        "/*.ico",
                        "/*.woff",
                        "/*.woff2",
                        "/*.ttf",
                        "/**/*.html",
                        "/**/*.js",
                        "/**/*.css",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jpeg",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.ico",
                        "/**/*.woff",
                        "/**/*.woff2",
                        "/**/*.ttf",
                        //前端 SPA 路由
                        "/main/**",
                        //项目源码下载（保留的 @Deprecated 端点，避免 token 空时
                        //RequestMapping 兜底走 static resource handler 抛 NoResourceFoundException）
                        "/project/download/**",
                        //按 code 加载项目（同上）
                        "/project/load/code/**"
                );
    }
}
