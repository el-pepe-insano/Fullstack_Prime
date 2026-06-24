package com.Nopki.autenticacion.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nopki - Servicio de Autenticacion")
                        .version("1.0.0")
                        .description("Microservicio encargado de la gestion de usuarios, roles y autenticacion JWT")
                        .contact(new Contact().name("Equipo Nopki")));
    }
}