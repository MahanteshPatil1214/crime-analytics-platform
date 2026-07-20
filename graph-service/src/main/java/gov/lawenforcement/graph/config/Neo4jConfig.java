package gov.lawenforcement.graph.config;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PreDestroy;

@Configuration
public class Neo4jConfig {

    @Value("${spring.neo4j.uri:bolt://localhost:7687}")
    private String neo4jUri;

    @Value("${spring.neo4j.authentication.username:neo4j}")
    private String neo4jUser;

    @Value("${NEO4J_PASSWORD:${spring.neo4j.authentication.password:}}")
    private String neo4jPassword;

    private Driver driver;

    @Bean
    public Driver neo4jDriver() {
        driver = GraphDatabase.driver(neo4jUri, AuthTokens.basic(neo4jUser, neo4jPassword));
        return driver;
    }

    @PreDestroy
    public void closeDriver() {
        if (driver != null) driver.close();
    }
}
