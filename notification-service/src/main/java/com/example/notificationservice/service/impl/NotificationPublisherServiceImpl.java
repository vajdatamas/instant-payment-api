package com.example.notificationservice.service.impl;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.OutboxEventStatus;
import com.example.dao.repository.OutboxEventRepository;
import com.example.notificationservice.service.NotificationPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationPublisherServiceImpl implements NotificationPublisherService {


    private final OutboxEventRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private static final String TOPIC = "transaction-events";
    private static final int MAX_RETRY = 5;

    @Override
    @Scheduled(fixedDelay = 3000)
    public void publishPendingEvents() {
        final List<OutboxEvent> events = outboxRepository.findByOutboxEventStatusIn(List.of(OutboxEventStatus.PENDING, OutboxEventStatus.FAILED));

        for (final OutboxEvent event : events) {
            try {
                kafkaTemplate.send(TOPIC, String.valueOf(event.getAggregateId()), event.getPayload());
                event.setOutboxEventStatus(OutboxEventStatus.SENT);
                log.info("Sent event with ID={} to topic '{}'", event.getId(), TOPIC);
            } catch (final Exception e) {
                log.error("Failed to send event with ID={}: {}", event.getId(), e.getMessage());
                event.setOutboxEventStatus(OutboxEventStatus.FAILED);
                event.setRetryCount(event.getRetryCount() + 1);

                if (event.getRetryCount() >= MAX_RETRY) {
                    log.warn("Max retry reached for event ID={}", event.getId());
                    event.setOutboxEventStatus(OutboxEventStatus.FAILED);
                } else {
                    event.setOutboxEventStatus(OutboxEventStatus.FAILED);
                }
            }
        }

        outboxRepository.saveAll(events);
    }
}

