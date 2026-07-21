package gov.lawenforcement.incident.config;

import jakarta.annotation.PostConstruct;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbInitializer {

    private final JdbcTemplate jdbcTemplate;

    public DbInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @PostConstruct
    public void init() {
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS evidence (
                evidence_id SERIAL PRIMARY KEY,
                case_master_id INTEGER NOT NULL REFERENCES case_master(case_master_id) ON DELETE CASCADE,
                file_name VARCHAR(255) NOT NULL,
                original_name VARCHAR(255) NOT NULL,
                file_type VARCHAR(100),
                file_size BIGINT,
                description TEXT,
                uploaded_by INTEGER REFERENCES employee(employee_id),
                upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                storage_path VARCHAR(500) NOT NULL
            )
        """);
    }
}
