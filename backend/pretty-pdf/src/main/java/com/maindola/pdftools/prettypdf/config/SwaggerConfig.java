package com.maindola.pdftools.prettypdf.config;

import com.maindola.pdftools.prettypdf.controller.RestApiController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    private static final Logger logger = LogManager.getLogger(RestApiController.class);

    @Bean
    public Docket produceApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.basePackage("com.maindola.pdftools")).build();
    }

    // Describe your apis
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Pretty-PDF REST Webservices")
                .description("This page lists the apis for Pretty-PDF-Webservices.")
                .contact(new Contact("Amit Maindola",
                        "https://www.linkedin.com/in/amit-maindola-51801423/",
                        "maindola.amit@gmail.com"))
                .version(System.getenv("VERSION")).build();
    }
}
