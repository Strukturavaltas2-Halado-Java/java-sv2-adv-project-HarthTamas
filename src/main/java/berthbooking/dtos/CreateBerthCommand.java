package berthbooking.dtos;

import berthbooking.model.BerthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateBerthCommand {

    @NotNull
    @Schema(description="Code for the berth", example = "Wifi-01")
    @NotBlank(message = "Berths must have a code!")
    private String code;

    @NotNull
    @Schema(description="Length of the berth", example = "1000")
    @Positive(message = "The length of the berth must be positive number!")
    private int length;

    @NotNull
    @Schema(description="Width of the berth", example = "340")
    @Positive(message = "The width of the berth must be positive number!")
    private int width;

    @NotNull
    @Schema(description="Type of the berth", example = "POWER_WATER")
    private BerthType berthType;

}
