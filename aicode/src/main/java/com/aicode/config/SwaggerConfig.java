package com.aicode.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.annotations.ApiIgnore;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger API文档配置类
 * 用于配置Swagger2接口文档的各项参数
 *
 * @author hegaoye
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 创建API文档的详细配置
     * 指定了文档类型、扫描的包路径、文档描述信息等
     *
     * @return Docket Swagger配置对象
     */
    @Bean
    public Docket buildDocket() {
        // 指定文档类型为Swagger2
        return new Docket(DocumentationType.SWAGGER_2)
                // 设置API基本信息
                .apiInfo(apiInfo())
                .select()
                // 指定要扫描的Controller包路径
                .apis(RequestHandlerSelectors.basePackage("com.aicode"))
                // 设置路径筛选，此处选择所有路径
                .paths(PathSelectors.any())
                .build()
                // 设置忽略使用@ApiIgnore注解的类或方法
                .ignoredParameterTypes(ApiIgnore.class);
    }

    /**
     * 创建API文档的基本信息
     * 包括文档标题、描述、版本、联系人信息等
     *
     * @return ApiInfo API文档基本信息对象
     */
    private ApiInfo apiInfo() {
        // 构建API基本信息
        return new ApiInfoBuilder()
                // 设置文档标题
                .title("订单系统 RESTful APIs")
                // 设置文档描述
                .description("订单系统接口文档详细说明")
                // 设置联系人信息（名称、网址、邮箱）
                .contact(new Contact(
                        "order",
                        "http://www.order.com/",
                        "order"))
                // 设置服务条款URL
                .termsOfServiceUrl("http://www.order.com/")
                // 设置API版本号
                .version("1.0")
                .build();
    }
}