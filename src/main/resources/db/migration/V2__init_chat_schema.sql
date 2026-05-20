CREATE TABLE IF NOT EXISTS conversations (
    id BIGSERIAL PRIMARY KEY,
    type VARCHAR(20) NOT NULL,
    support_customer_user_id VARCHAR(128),
    last_message_preview VARCHAR(500),
    last_message_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

ALTER TABLE conversations
    DROP CONSTRAINT IF EXISTS conversations_type_check;

ALTER TABLE conversations
    ADD CONSTRAINT conversations_type_check
        CHECK (type IN ('DIRECT', 'SUPPORT'));

CREATE UNIQUE INDEX IF NOT EXISTS uk_conversations_support_customer
    ON conversations (support_customer_user_id)
    WHERE support_customer_user_id IS NOT NULL;

CREATE TABLE IF NOT EXISTS conversation_participants (
    id BIGSERIAL PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    user_id VARCHAR(128) NOT NULL,
    joined_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_conversation_participants_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_conversation_participants_conversation_user
    ON conversation_participants (conversation_id, user_id);

CREATE TABLE IF NOT EXISTS messages (
    id UUID PRIMARY KEY,
    conversation_id BIGINT NOT NULL,
    sender_id VARCHAR(128) NOT NULL,
    content TEXT NOT NULL,
    client_message_id VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_messages_conversation
        FOREIGN KEY (conversation_id) REFERENCES conversations (id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_messages_conversation_created_at
    ON messages (conversation_id, created_at DESC);
