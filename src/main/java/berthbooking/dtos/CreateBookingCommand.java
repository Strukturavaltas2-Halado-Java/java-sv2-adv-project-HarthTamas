package berthbooking.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotNull
    @Schema(description="Name of the boat", example = "Flyer")
    @NotBlank(message = "Boats must have a name!")
    private String boatName;

    @NotNull
    @Schema(description="Registration number of the boat", example = "H-1212556")
    @NotBlank(message = "Boats must have registration number!")
    private String registrationNumber;

    @NotNull
    @Schema(description="Length of the boat", example = "750")
    @Positive(message = "The length of the boat must be positive number!")
    private int boatLength;

    @NotNull
    @Schema(description="Width of the boat", example = "254")
    @Positive(message = "The width of the boat must be positive number!")
    private int boatWidth;

    @NotNull
    @Schema(description="The firs date of the booking", example = "2022-08-29")
    @FutureOrPresent(message = "This date must be in the present or in the future!")
    private LocalDate fromDate;

    @NotNull
    @Schema(description="Number of days to book", example = "3")
    @Max(value = 3, message = "Must be a positive number and equal or less than "+ MAX_DAYS)
    @Positive(message = "Must be a positive number and less than "+ MAX_DAYS)
    private int numberOfDays;

}
