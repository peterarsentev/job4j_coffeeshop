CREATE TABLE last_event(
    order_id INT PRIMARY KEY,
    event_type INT
);

CREATE TABLE events (
    id SERIAL PRIMARY KEY,
    event_type INT,
    order_id INT,
    employer_id INT,
    time TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW()
);