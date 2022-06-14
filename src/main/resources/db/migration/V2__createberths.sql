CREATE TABLE berths
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    code       VARCHAR(255)          NULL,
    length     INT                   NULL,
    width      INT                   NULL,
    berth_type VARCHAR(255)          NULL,
    port_id    BIGINT                NULL,
    CONSTRAINT pk_berths PRIMARY KEY (id)
);

ALTER TABLE berths
    ADD CONSTRAINT FK_BERTHS_ON_PORT FOREIGN KEY (port_id) REFERENCES ports (id);