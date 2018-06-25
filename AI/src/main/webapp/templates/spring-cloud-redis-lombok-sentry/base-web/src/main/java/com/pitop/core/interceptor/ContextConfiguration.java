package ${basePackage}.core.interceptor;

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
    ContextInterceptor contextInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(contextInterceptor).addPathPatterns("/**");//全局拦截器
        registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");//登录拦截器
    }
}
