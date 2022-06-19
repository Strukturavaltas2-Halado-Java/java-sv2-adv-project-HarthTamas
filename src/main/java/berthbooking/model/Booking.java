package berthbooking.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Booking {

    @NotNull
    @Schema(description="Name of the boat", example = "Flyer")
    @Column(name="boat_name")
    private String boatName;

    @NotNull
    @Schema(description="Registration number of the boat", example = "H-1212556")
    @Column(name="reg_nr")
    private String registrationNumber;

    @NotNull
    @Schema(description="Length of the boat", example = "750")
    @Column(name="boat_length")
    private int boatLength;

    @NotNull
    @Schema(description="Width of the boat", example = "254")
    @Column(name="boat_width")
    private int boatWidth;


    @Schema(description="System generated value")
    @Column(name="time_of_booking")
    private LocalDateTime timeOfBooking;

    @NotNull
    @Schema(description="The firs date of the booking", example = "2022-08-29")
    @Column(name="from_date")
    private LocalDate fromDate;

    @NotNull
    @Schema(description="Number of days to book", example = "3")
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
