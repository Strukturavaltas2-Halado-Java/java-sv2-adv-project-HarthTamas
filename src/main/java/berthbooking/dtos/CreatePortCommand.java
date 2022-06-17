package berthbooking.dtos;

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

    @NotBlank(message = "Ports must have a name!")
    private String portName;

    @Email(message = "Must be a valid email format!")
    private String email;

    @Positive(message = "The number of guest berths must be positive number!")
    private int numberOfGuestBerths;
}
