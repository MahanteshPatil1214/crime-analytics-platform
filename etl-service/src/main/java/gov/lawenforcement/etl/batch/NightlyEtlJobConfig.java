package gov.lawenforcement.etl.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Collections;
import java.util.List;

@Configuration
public class NightlyEtlJobConfig {

    @Bean
    public Job nightlyEtlJob(JobRepository jobRepository,
                              Step extractStep,
                              Step transformStep,
                              Step loadGraphStep) {
        return new JobBuilder("nightly-etl-job", jobRepository)
                .start(extractStep)
                .next(transformStep)
                .next(loadGraphStep)
                .preventRestart()
                .build();
    }

    @Bean
    public Step extractStep(JobRepository jobRepository, PlatformTransactionManager txManager) {
        return new StepBuilder("extract-incidents", jobRepository)
                .<String, String>chunk(500, txManager)
                .reader(noOpReader())
                .writer(noOpWriter())
                .faultTolerant()
                .skipLimit(100)
                .skip(Exception.class)
                .build();
    }

    private ItemReader<String> noOpReader() {
        return new ItemReader<>() {
            private boolean done = false;
            @Override
            public String read() {
                if (done) return null;
                done = true;
                return "ETL_PLACEHOLDER";
            }
        };
    }

    private ItemWriter<String> noOpWriter() {
        return items -> {};
    }
}
