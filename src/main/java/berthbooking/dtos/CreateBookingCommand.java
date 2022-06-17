package berthbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateBookingCommand {

    private static final int MAX_DAYS = 3;

    @NotBlank(message = "Boats must have a name!")
    private String boatName;

    @NotBlank(message = "Boats must have registration number!")
    private String registrationNumber;

    @Positive(message = "The length of the boat must be positive number!")
    private int boatLength;

    @Positive(message = "The width of the boat must be positive number!")
    private int boatWidth;

    @NotNull(message = "First date of the booking must be a valid date ('yyyy-mm-dd')")
    private LocalDate fromDate;

    @Max(value = 3, message = "Must be a positive number, and less than "+ MAX_DAYS)
    @Positive(message = "Must be a positive number and less than "+ MAX_DAYS)
    private int numberOfDays;

}
