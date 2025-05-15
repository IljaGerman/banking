--liquibase formatted sql
--changeset i.german:v2_insert_data

INSERT INTO banking.user (name, date_of_birth, password)
VALUES ('Иванов Иван Иванович', '1990-05-15', '$2y$10$RSVpMye3X6qeabJjmk4nxud7XPyz/zq/ay6ieAyHq4ShAOKqZvguS'),
       ('Петрова Анна Сергеевна', '1985-08-22', '$2y$10$RSVpMye3X6qeabJjmk4nxud7XPyz/zq/ay6ieAyHq4ShAOKqZvguS'),
       ('Сидоров Алексей Владимирович', '1978-11-30', '$2y$10$RSVpMye3X6qeabJjmk4nxud7XPyz/zq/ay6ieAyHq4ShAOKqZvguS!'),
       ('Кузнецова Екатерина Дмитриевна', '1995-02-14', '$2y$10$RSVpMye3X6qeabJjmk4nxud7XPyz/zq/ay6ieAyHq4ShAOKqZvguS'),
       ('Смирнов Денис Олегович', '1982-07-09', '$2y$10$RSVpMye3X6qeabJjmk4nxud7XPyz/zq/ay6ieAyHq4ShAOKqZvguS');

INSERT INTO banking.email_data (user_id, email)
VALUES (1, 'ivanov.ii@example.com'),
       (2, 'petrova.anna@example.com'),
       (3, 'sidorov.av@example.com'),
       (4, 'kuznetsova.ed@example.com'),
       (5, 'smirnov.do@example.com');

INSERT INTO banking.phone_data (user_id, phone)
VALUES (1, '+79161234567'),
       (2, '+79031234568'),
       (3, '+79261234569'),
       (4, '+79111234570'),
       (5, '+79091234571');

INSERT INTO banking.account (user_id, balance)
VALUES (1, 150000.50),
       (2, 2500000.00),
       (3, 75000.25),
       (4, 1200000.75),
       (5, 500000.00);