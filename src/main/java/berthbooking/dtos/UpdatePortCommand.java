package berthbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePortCommand {

    @PositiveOrZero(message = "The number of guest berths must be positive or zero!")
    private int numberOfGuestBerths;

}
