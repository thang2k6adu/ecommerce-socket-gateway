ALTER TABLE conversations
    ADD COLUMN IF NOT EXISTS support_customer_user_id VARCHAR(128);

ALTER TABLE conversations
    DROP CONSTRAINT IF EXISTS conversations_type_check;

ALTER TABLE conversations
    ADD CONSTRAINT conversations_type_check
        CHECK (type IN ('DIRECT', 'SUPPORT'));

CREATE UNIQUE INDEX IF NOT EXISTS uk_conversations_support_customer
    ON conversations (support_customer_user_id)
    WHERE support_customer_user_id IS NOT NULL;
