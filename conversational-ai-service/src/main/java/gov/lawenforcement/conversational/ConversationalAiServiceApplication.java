package gov.lawenforcement.conversational;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class ConversationalAiServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConversationalAiServiceApplication.class, args);
    }
}
