CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS tasks
(
    id           UUID NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    title        VARCHAR(255) NOT NULL,
    description  TEXT,
    status       VARCHAR(50)  NOT NULL DEFAULT 'TODO',
    priority     VARCHAR(50)  NOT NULL DEFAULT 'LOW',
    due_date     DATE,
    assigned_to  VARCHAR(255),
    created_by   VARCHAR(255) NOT NULL,
    created_at   TIMESTAMP,
    updated_at   TIMESTAMP,
    completed_at TIMESTAMP
);

-- CREATE INDEX IF NOT EXISTS idx__event_identity ON tasks (event, identity_id);