package berthbooking.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePortCommand {

    @NotNull
    @Schema(description="Name of the town where port is located", example = "Keszthely")
    @NotBlank(message = "Ports must have a name!")
    private String town;

    @NotNull
    @Schema(description="Email of the port", example = "keszthely@balaport.hu")
    @Email(message = "Must be a valid email format!")
    private String email;

    @NotNull
    @Schema(description="Number of guest berths", example = "5")
    @PositiveOrZero(message = "The number of guest berths must be positive or zero!")
    private int numberOfGuestBerths;
}
