package gov.lawenforcement.etl.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtlScheduler {

    private final JobLauncher jobLauncher;
    private final Job nightlyEtlJob;

    @Scheduled(cron = "0 0 2 * * ?")
    public void runNightlyEtl() {
        log.info("Starting nightly ETL at {}", java.time.Instant.now());
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("runTime", System.currentTimeMillis())
                    .addString("date", java.time.LocalDate.now().toString())
                    .toJobParameters();
            JobExecution execution = jobLauncher.run(nightlyEtlJob, params);
            if (execution.getStatus() == BatchStatus.COMPLETED) {
                log.info("Nightly ETL completed: {}", execution.getExitStatus());
            } else {
                log.error("Nightly ETL failed: {}", execution.getStatus());
            }
        } catch (Exception e) {
            log.error("ETL launch failed", e);
        }
    }
}
