package com.lumendecision.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lumenDecisionApiOpenApi() {
        Contact contact = new Contact()
                .name("Goncalves95")
                .url("https://github.com/Goncalves95");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Lumen Decision API")
                .description("Open source financial decision scoring engine. "
                        + "Calculate how financial decisions impact your score before making them.")
                .version("1.0.0")
                .contact(contact)
                .license(license);

        Server localServer = new Server()
                .url("http://localhost:8082")
                .description("Local server");

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer))
                .externalDocs(new ExternalDocumentation()
                        .description("GitHub Repository")
                        .url("https://github.com/Goncalves95/lumen-decision-api"));
    }
}
