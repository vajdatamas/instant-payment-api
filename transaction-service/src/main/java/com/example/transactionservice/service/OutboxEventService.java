package com.example.transactionservice.service;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.AggregateType;
import com.example.dao.model.type.EventType;
import com.example.dao.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    public void saveTransactionCreatedEvent(final UUID transactionId) {
        final OutboxEvent event = new OutboxEvent();
        event.setAggregateType(AggregateType.TRANSACTION);
        event.setAggregateId(transactionId);
        event.setEventType(EventType.CREATED);
        event.setPayload("{\"transactionId\":\"" + transactionId + "\"}");
        event.setProcessed(false);
        event.setCreatedAt(ZonedDateTime.now());

        outboxEventRepository.save(event);
    }
}
