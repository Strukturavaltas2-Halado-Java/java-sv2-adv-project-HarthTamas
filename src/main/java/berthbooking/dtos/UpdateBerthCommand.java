package berthbooking.dtos;

import berthbooking.model.BerthType;
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
public class UpdateBerthCommand {

    @NotBlank(message = "Berths must have a code!")
    private String code;

    @Positive(message = "The length of the berth must be positive number!")
    private int length;

    @Positive(message = "The width of the berth must be positive number!")
    private int width;

    @NotNull(message = "Type can't be Null!!")
    private BerthType berthType;
}
