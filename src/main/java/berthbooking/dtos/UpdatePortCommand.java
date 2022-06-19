package berthbooking.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePortCommand {

    @NotNull
    @Schema(description="Number of guest berths", example = "5")
    @PositiveOrZero(message = "The number of guest berths must be positive or zero!")
    private int numberOfGuestBerths;

}
