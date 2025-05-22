package com.brasileiras.ecommerce_api.config;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ECommerce Brasileiras API")
                        .version("v1")
                        .description("Gerenciamento do ECommerce Brasileiras API")
                        .contact(new Contact()
                                .name("Andrezza Almeida")
                                .url("https://github.com/Andretsaalmeida/ecommerce.git"))

                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org"))
                )
                .servers(List.of(
                        new Server().url("/") // ← Isso remove o "generated"
                ));
    }

}
