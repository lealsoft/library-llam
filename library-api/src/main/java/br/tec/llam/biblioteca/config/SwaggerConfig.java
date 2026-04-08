package br.tec.llam.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LLAM Biblioteca API")
                        .version("1.0.0")
                        .description("API REST para gerenciamento de biblioteca digital multi-tenant")
                        .contact(new Contact()
                                .name("LLAM")
                                .url("https://llam.tec.br")
                                .email("contato@llam.tec.br"))
                        .license(new License()
                                .name("Proprietário")
                                .url("https://llam.tec.br")));
    }
}
