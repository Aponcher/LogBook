-- 1. Add a new column as timestamptz
ALTER TABLE activity_log ADD COLUMN timestamp_utc TIMESTAMPTZ;

ALTER TABLE activity_log ADD COLUMN user_id
    VARCHAR(50) NOT NULL DEFAULT 'test-user';
