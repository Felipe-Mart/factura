package com.microservicio.factura.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()

                        .title("API Servicio de Sucursales")

                        .version("1.0")

                        .description(
                                "Microservicio encargado de administrar sucursales, políticas y turnos de empleados.")

                        .contact(new Contact()
                                .name("Felipe Martínez")
                                .email("felipe@email.com")));
    }

}
