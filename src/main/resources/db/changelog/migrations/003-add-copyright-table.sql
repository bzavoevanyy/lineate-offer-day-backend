create table copyright
(
    id              BIGSERIAL PRIMARY KEY,
    royalty         DOUBLE,
    start_time      TIMESTAMP,
    expiry_time     TIMESTAMP,
    recording_id    BIGINT REFERENCES recording (id),
    company_id      BIGINT REFERENCES company (id)
);
