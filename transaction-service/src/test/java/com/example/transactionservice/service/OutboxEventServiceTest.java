package com.example.transactionservice.service;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.AggregateType;
import com.example.dao.model.type.OutboxEventStatus;
import com.example.dao.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OutboxEventServiceTest {

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @InjectMocks
    private OutboxEventService outboxEventService;

    @Test
    void saveTransactionCreatedEvent_shouldSaveEvent() {
        final var transactionId = UUID.randomUUID();

        outboxEventService.saveTransactionCreatedEvent(transactionId);

        final var expectedPayload = "{\"transactionId\":\"" + transactionId + "\"}";

        final var captor = ArgumentCaptor.forClass(OutboxEvent.class);
        verify(outboxEventRepository).save(captor.capture());

        final var event = captor.getValue();
        assertThat(event.getAggregateType()).isEqualTo(AggregateType.TRANSACTION);
        assertThat(event.getOutboxEventStatus()).isEqualTo(OutboxEventStatus.PENDING);
        assertThat(event.getPayload()).isEqualTo(expectedPayload);
        assertThat(event.getAggregateId()).isEqualTo(transactionId);
        assertThat(event.getCreatedAt()).isNotNull();
    }
}
