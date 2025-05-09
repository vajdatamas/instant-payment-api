package com.example.notificationservice.service.impl;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.OutboxEventStatus;
import com.example.dao.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationPublisherServiceImplTest {

    @Mock
    private OutboxEventRepository outboxRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private NotificationPublisherServiceImpl notificationPublisherService;

    @Test
    void shouldMarkEventAsSent_whenKafkaSendSucceeds() {
        final OutboxEvent event = new OutboxEvent();
        event.setId(1L);
        event.setAggregateId(UUID.randomUUID());
        event.setPayload("{\"key\":\"value\"}");
        event.setOutboxEventStatus(OutboxEventStatus.PENDING);
        event.setRetryCount(0);

        when(outboxRepository.findByOutboxEventStatusIn(anyList()))
                .thenReturn(List.of(event));

        notificationPublisherService.publishPendingEvents();

        assertEquals(OutboxEventStatus.SENT, event.getOutboxEventStatus());
        verify(kafkaTemplate).send(anyString(), anyString(), eq(event.getPayload()));
        verify(outboxRepository).saveAll(anyList());
    }

    @Test
    void shouldRetryAndMarkAsFailed_whenKafkaSendFails() {
        final OutboxEvent event = new OutboxEvent();
        event.setId(1L);
        event.setAggregateId(UUID.randomUUID());
        event.setPayload("{\"key\":\"value\"}");
        event.setOutboxEventStatus(OutboxEventStatus.PENDING);
        event.setRetryCount(2);

        when(outboxRepository.findByOutboxEventStatusIn(anyList()))
                .thenReturn(List.of(event));

        doThrow(new RuntimeException("Kafka send failed"))
                .when(kafkaTemplate).send(anyString(), anyString(), anyString());

        notificationPublisherService.publishPendingEvents();

        assertEquals(OutboxEventStatus.FAILED, event.getOutboxEventStatus());
        assertEquals(3, event.getRetryCount());
        verify(outboxRepository).saveAll(anyList());
    }
}