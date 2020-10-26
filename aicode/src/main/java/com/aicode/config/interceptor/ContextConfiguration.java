package com.aicode.config.interceptor;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by lixin on 2018/6/23.
 */
@Configuration
public class ContextConfiguration extends WebMvcConfigurationSupport {

    @Resource
    private ContextInterceptor contextInterceptor;
    @Resource
    private LoginInterceptor loginInterceptor;


    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();
        //自定义配置...
        FastJsonConfig config = new FastJsonConfig();
        config.setSerializerFeatures(SerializerFeature.WriteDateUseDateFormat, SerializerFeature.WriteMapNullValue, SerializerFeature.DisableCircularReferenceDetect);
        //config.set ...
        converter.setFastJsonConfig(config);
        converters.add(converter);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        super.addResourceHandlers(registry);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)
                .setUseTrailingSlashMatch(true);
    }

    /**
     * 拦截某个请求跳转固定位置
     *
     * @param registry
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/index");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(contextInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger-resources/**");//全局拦截器
//        registry.addInterceptor(loginInterceptor).addPathPatterns("/**").excludePathPatterns("/swagger-resources/**");//登录拦截器
    }
}
