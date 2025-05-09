package com.example.transactionservice.service;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.AggregateType;
import com.example.dao.model.type.OutboxEventStatus;
import com.example.dao.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventService {

    private final OutboxEventRepository outboxEventRepository;

    public void saveTransactionCreatedEvent(final UUID transactionId) {
        log.info("Start creating event");
        final OutboxEvent event = new OutboxEvent()
                .setAggregateType(AggregateType.TRANSACTION)
                .setAggregateId(transactionId)
                .setOutboxEventStatus(OutboxEventStatus.PENDING)
                .setPayload("{\"transactionId\":\"" + transactionId + "\"}")
                .setCreatedAt(ZonedDateTime.now());

        outboxEventRepository.save(event);
        log.info("Event has been created");
    }
}
