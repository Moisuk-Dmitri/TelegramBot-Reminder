-- liquibase formatted sql

-- changeset dmitri:1
CREATE TABLE events (
    id SERIAL,
    chat_id TEXT,
    event_text TEXT,
    event_date DATE
)

-- changeset dmitri:2
ALTER TABLE event
ADD PRIMARY KEY (id)

--changeset dmitri:3
ALTER TABLE event
ALTER COLUMN event_date TYPE timestamp