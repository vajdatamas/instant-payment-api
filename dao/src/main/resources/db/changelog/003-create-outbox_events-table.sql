CREATE TABLE outbox_events (
    id BIGSERIAL PRIMARY KEY,
    aggregate_type VARCHAR(255) NOT NULL,
    aggregate_id UUID NOT NULL,
    outbox_event_status VARCHAR(255) NOT NULL,
    payload TEXT NOT NULL,
    retry_count INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL
);
