CREATE TABLE ports
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    port_name          VARCHAR(255)          NULL,
    email              VARCHAR(255)          NULL,
    nr_of_guest_berths INT                   NULL,
    CONSTRAINT pk_ports PRIMARY KEY (id)
);