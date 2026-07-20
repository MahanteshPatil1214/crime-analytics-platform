package gov.lawenforcement.messaging;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic incidentCreatedTopic() {
        return TopicBuilder.name("crime.incident.created").partitions(12).replicas(3).build();
    }

    @Bean
    public NewTopic incidentUpdatedTopic() {
        return TopicBuilder.name("crime.incident.updated").partitions(12).replicas(3).build();
    }

    @Bean
    public NewTopic personIndexedTopic() {
        return TopicBuilder.name("crime.person.indexed").partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic graphSyncTopic() {
        return TopicBuilder.name("crime.graph.sync").partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic alertNotificationTopic() {
        return TopicBuilder.name("crime.alert.notification").partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic chatEventTopic() {
        return TopicBuilder.name("crime.chat.events").partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic financialAlertTopic() {
        return TopicBuilder.name("crime.financial.alert").partitions(6).replicas(3).build();
    }

    @Bean
    public NewTopic etlCompletionTopic() {
        return TopicBuilder.name("crime.etl.completed").partitions(3).replicas(2).build();
    }
}
