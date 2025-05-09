package integration;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.AggregateType;
import com.example.dao.model.type.OutboxEventStatus;
import com.example.dao.repository.OutboxEventRepository;
import com.example.notificationservice.NotificationServiceApplication;
import com.example.notificationservice.service.impl.NotificationPublisherServiceImpl;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(classes = NotificationServiceApplication.class)
@Testcontainers
@ActiveProfiles("test")
public class NotificationPublisherIT {

    @Container
    static KafkaContainer kafka = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.2.1")
    );

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideProps(final DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private OutboxEventRepository outboxRepository;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private NotificationPublisherServiceImpl notificationPublisherService;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    private static final String TOPIC = "transaction-events";

    @BeforeEach
    void setup() {
        final OutboxEvent event = new OutboxEvent();
        event.setAggregateId(UUID.randomUUID());
        event.setOutboxEventStatus(OutboxEventStatus.PENDING);
        event.setPayload("{\"example\": true}");
        event.setAggregateType(AggregateType.TRANSACTION);
        event.setRetryCount(0);
        event.setCreatedAt(ZonedDateTime.now());
        outboxRepository.save(event);
    }

    @Test
    void shouldPublishEventToKafkaAndUpdateStatus() throws InterruptedException {

        final Consumer<String, String> consumer = consumerFactory.createConsumer();
        consumer.subscribe(List.of(TOPIC));


        notificationPublisherService.publishPendingEvents();


        ConsumerRecords<String, String> records = ConsumerRecords.empty();
        int attempts = 5;
        while (records.isEmpty() && attempts-- > 0) {
            records = consumer.poll(Duration.ofSeconds(1));
        }

        assertFalse(records.isEmpty(), "No Kafka message received");


        final List<OutboxEvent> updatedEvents = outboxRepository.findAll();
        assertEquals(OutboxEventStatus.SENT, updatedEvents.get(0).getOutboxEventStatus());
    }
}
