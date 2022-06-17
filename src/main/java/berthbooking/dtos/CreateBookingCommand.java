package berthbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;
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

    @FutureOrPresent(message = "This date must be in the present or in the future!")
    private LocalDate fromDate;

    @Max(value = 3, message = "Must be a positive number and less than "+ MAX_DAYS)
    @Positive(message = "Must be a positive number and less than "+ MAX_DAYS)
    private int numberOfDays;

}
