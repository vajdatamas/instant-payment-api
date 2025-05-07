CREATE TABLE IF NOT EXISTS transactions (
    id SERIAL PRIMARY KEY,
    transaction_id UUID NOT NULL UNIQUE,
    amount DECIMAL(18, 2) NOT NULL,
    sender_account VARCHAR(255) NOT NULL,
    receiver_account VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);