package ${basePackage}.core.config;

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
                .title("庞帝中国 RESTful APIs")
                .description("郑州仁中和科技有限公司保留所有代码著作权.如有任何疑问请访问官方网站与我们联系.代码只针对特定客户使用，不得在未经允许或授权的情况下对外传播扩散.恶意传播者，法律后果自行承担.本代码仅用于AI-Code.请访问  https://gitee.com/helixin/AI-Code")
                .contact(new Contact("郑州仁中和科技有限公司","http://www.rzhkj.com/","hegaoye@qq.com"))
                .termsOfServiceUrl("https://gitee.com/helixin/AI-Code")
                .version("1.0")
                .build();
    }

}