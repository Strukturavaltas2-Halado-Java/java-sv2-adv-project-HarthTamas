CREATE TABLE berth_bookings
(
    berth_id        BIGINT       NOT NULL,
    boat_name       VARCHAR(255) NULL,
    reg_nr          VARCHAR(255) NULL,
    boat_length     INT          NULL,
    boat_width      INT          NULL,
    time_of_booking datetime     NULL,
    from_date       date         NULL,
    nr_of_day       INT          NULL
);

ALTER TABLE berth_bookings
    ADD CONSTRAINT fk_berth_bookings_on_berth FOREIGN KEY (berth_id) REFERENCES berths (id);