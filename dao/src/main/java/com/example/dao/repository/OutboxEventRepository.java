package com.example.dao.repository;

import com.example.dao.model.OutboxEvent;
import com.example.dao.model.type.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {
    List<OutboxEvent> findByOutboxEventStatusIn(final List<OutboxEventStatus> statuses);
}
