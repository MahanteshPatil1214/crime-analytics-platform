package gov.lawenforcement.graph;

import gov.lawenforcement.graph.service.GraphPopulatorService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jDataAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = {Neo4jDataAutoConfiguration.class})
@EnableDiscoveryClient
public class GraphServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner autoPopulate(GraphPopulatorService populator) {
        return args -> {
            System.out.println(">>> Auto-populating Neo4j graph from PostgreSQL...");
            int attempt = 0;
            while (attempt < 5) {
                try {
                    populator.populateAll();
                    System.out.println(">>> Graph population complete!");
                    return;
                } catch (Exception e) {
                    attempt++;
                    System.out.println(">>> Attempt " + attempt + "/5 failed: " + e.getMessage() + " - retrying in 5s...");
                    Thread.sleep(5000);
                }
            }
            System.out.println(">>> Graph population failed after 5 attempts. Use POST /api/v1/graph/populate to retry.");
        };
    }
}
