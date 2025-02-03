package com.devsuperior.dsmovie.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition
public class OpenApiConfig {

    @Bean
    public OpenAPI dsMovieApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("DS Movie API")
                        .description("DS Movie Reference Project")
                        .version("v1.0")
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://localhost:8080/swagger-ui/index.html")));
    }
}
