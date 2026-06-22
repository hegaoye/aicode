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
                        //登录
                        "/login/signin",
                        "/login/reg",
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
                        //前端 SPA 路由
                        "/main/**"
                );
    }
}
