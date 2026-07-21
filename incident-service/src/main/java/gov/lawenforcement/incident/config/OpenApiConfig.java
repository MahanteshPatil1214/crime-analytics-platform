package gov.lawenforcement.incident.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI crimeAnalyticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Incident Service API")
                        .description("REST API for FIR case management, lookups, and advanced search. Part of the Crime Analytics Platform.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Crime Analytics Team")));
    }
}
