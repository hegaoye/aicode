package ${basePackage}.core.tools;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
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
 * @author lixin
 * @version 2017年5月27日 上午9:50:59
 */
@Configuration
@EnableWebMvc
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket buildDocket() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("${basePackage}"))//controller路径
                .paths(PathSelectors.any())
                .build()
                .ignoredParameterTypes(ApiIgnore.class);
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("AI-Code RESTful APIs")
                .description("郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.请访问  http://www.rzhkj.com/")
                .contact(new Contact("郑州仁中和科技有限公司", "http://www.rzhkj.com/", "hegaoye@qq.com"))
                .termsOfServiceUrl("http://www.rzhkj.com/")
                .version("1.0")
                .build();
    }

}