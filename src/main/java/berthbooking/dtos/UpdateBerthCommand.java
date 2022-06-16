package berthbooking.dtos;

import berthbooking.model.BerthType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateBerthCommand {

    private String code;
    private int length;
    private int width;
    private BerthType berthType;
}
