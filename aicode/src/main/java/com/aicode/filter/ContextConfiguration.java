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
        //        registry.addResourceHandler("doc.html")
        //                .addResourceLocations("classpath:/META-INF/resources/");
        //        registry.addResourceHandler("/webjars/**")
        //                .addResourceLocations("classpath:/META-INF/resources/webjars/");
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
        //全局拦截器
        registry.addInterceptor(contextInterceptor)
                .addPathPatterns("/**");
        //        registry.addInterceptor(loginInterceptor)
        //                .addPathPatterns("/**");
    }
}
