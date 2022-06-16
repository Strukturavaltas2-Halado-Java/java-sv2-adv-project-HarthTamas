package berthbooking.dtos;

import berthbooking.model.BerthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBerthCommand {

    @NotBlank
    private String code;
    private int length;
    private int width;
    private BerthType berthType;
}
