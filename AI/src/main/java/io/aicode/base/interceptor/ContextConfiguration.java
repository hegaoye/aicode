package io.aicode.base.interceptor;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.Resource;

/**
 * Created by lixin on 2018/6/23.
 */
@SpringBootConfiguration
public class ContextConfiguration extends WebMvcConfigurerAdapter {

    @Resource
    private ContextInterceptor contextInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(contextInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger-resources/**");//全局拦截器
//        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger-resources/**");//登录拦截器
    }
}
