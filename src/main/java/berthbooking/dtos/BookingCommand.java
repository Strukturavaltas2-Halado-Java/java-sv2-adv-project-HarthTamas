package berthbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingCommand {

    private String boatName;
    private String registrationNumber;
    private int boatLength;
    private int boatWidth;
    private LocalDate fromDate;

    @Max(3)
    @Positive
    private int numberOfDays;

}
