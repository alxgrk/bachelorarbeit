package com.alxgrk.level3;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.alxgrk.level3.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("alxgrk", "github.com/alxgrk/bachelorarbeit",
                "alexgirke@gmail.com");
        return new ApiInfo("Level 3", "Example for Level 3 of the Richardson Maturity Model",
                "1.0.0", ""/* TODO */, contact, "Apache Licenes 2.0",
                "http://www.apache.org/licenses/", Lists.newArrayList());
    }

}