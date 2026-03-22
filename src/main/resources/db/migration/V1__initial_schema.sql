CREATE TABLE app_user
(
    id            BIGSERIAL PRIMARY KEY,
    username      VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255)        NOT NULL,
    role          VARCHAR(50) DEFAULT 'USER'
);

CREATE TABLE flight
(
    id                BIGSERIAL PRIMARY KEY,
    departure_city    VARCHAR(255)   NOT NULL,
    destination_city  VARCHAR(255)   NOT NULL,
    departure_date    DATE           NOT NULL,
    departure_time    TIME           NOT NULL,
    airline           VARCHAR(255)   NOT NULL,
    price             DECIMAL(10, 2) NOT NULL,
    available_tickets INT            NOT NULL CHECK (available_tickets >= 0)
);

CREATE TABLE booking
(
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL,
    flight_id         BIGINT NOT NULL,
    booking_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES app_user (id),
    CONSTRAINT fk_flight FOREIGN KEY (flight_id) REFERENCES flight (id)
);

-- Seed Data: User 'felix.huber' with password 'password123' (SHA-256 hashed)
INSERT INTO app_user (username, password_hash)
VALUES ('felix.huber', 'ef92b778bafe771e89245b89ecbc08a44a4e166c06659911881f383d4473e94f');

-- Seed Data: Flights
INSERT INTO flight (departure_city, destination_city, departure_date, departure_time, airline, price, available_tickets)
VALUES ('Zürich', 'London', '2026-04-10', '08:00:00', 'Swiss', 150.00, 42),
       ('Zürich', 'Berlin', '2026-04-12', '14:30:00', 'EasyJet', 85.50, 12),
       ('Genf', 'Paris', '2026-04-10', '09:15:00', 'Air France', 120.00, 5),
       ('Zürich', 'New York', '2026-05-01', '13:00:00', 'Swiss', 650.00, 120);
