package berthbooking.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Booking {

    @Column(name="boat_name")
    private String boatName;

    @Column(name="reg_nr")
    private String registrationNumber;

    @Column(name="boat_length")
    private int boatLength;

    @Column(name="boat_width")
    private int boatWidth;

    @Column(name="time_of_booking")
    private LocalDateTime timeOfBooking;

    @Column(name="from_date")
    private LocalDate fromDate;

    @Column(name="nr_of_days")
    private int numberOfDays;


    public Booking(String boatName, String registrationNumber, int boatLength, int boatWidth, LocalDate fromDate, int numberOfDays) {
        this.boatName = boatName;
        this.registrationNumber = registrationNumber;
        this.boatLength = boatLength;
        this.boatWidth = boatWidth;
        this.fromDate = fromDate;
        this.numberOfDays = numberOfDays;
    }
}
