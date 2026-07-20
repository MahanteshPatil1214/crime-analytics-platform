package gov.lawenforcement.search;

import gov.lawenforcement.search.service.IndexingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class SearchServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner autoIndex(IndexingService indexingService) {
        return args -> {
            System.out.println(">>> Auto-indexing data into Elasticsearch...");
            int attempt = 0;
            while (attempt < 5) {
                try {
                    var result = indexingService.indexAll();
                    System.out.println(">>> Indexing complete: " + result);
                    return;
                } catch (Exception e) {
                    attempt++;
                    System.out.println(">>> Attempt " + attempt + "/5 failed: " + e.getMessage() + " - retrying in 5s...");
                    Thread.sleep(5000);
                }
            }
            System.out.println(">>> Indexing failed after 5 attempts. Use POST /api/v1/search/reindex to retry.");
        };
    }
}
