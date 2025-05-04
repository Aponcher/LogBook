-- 1. Add a new column as timestamptz
ALTER TABLE activity_log ADD COLUMN timestamp_utc TIMESTAMPTZ;
