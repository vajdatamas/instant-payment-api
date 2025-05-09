package com.example.transactionservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi transactionServiceApi() {
        return GroupedOpenApi.builder()
                .group("transaction-service")
                .packagesToScan("com.example.transactionservice.controller")
                .build();
    }

    @Bean
    public OpenAPI transactionServiceOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transaction Service API")
                        .description("API documentation for the Transaction microservice")
                        .version("1.0")
                );
    }
}
