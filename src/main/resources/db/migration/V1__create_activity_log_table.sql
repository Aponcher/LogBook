CREATE TABLE activity_log (
    id UUID primary key,
    type VARCHAR(50) NOT NULL,
    quantity INTEGER NOT NULL,
    unit VARCHAR(10),
    timestamp TIMESTAMP NOT NULL
);