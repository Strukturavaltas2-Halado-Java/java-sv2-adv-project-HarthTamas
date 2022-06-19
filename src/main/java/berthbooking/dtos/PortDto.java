package berthbooking.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortDto {

    private Long id;
    private String portName;
    private String email;
    private int numberOfGuestBerths;
    private List<BerthDto> berths = new ArrayList<>();

    public PortDto(String portName, String email, int numberOfGuestBerths) {
        this.portName = portName;
        this.email = email;
        this.numberOfGuestBerths = numberOfGuestBerths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PortDto portDto = (PortDto) o;
        return numberOfGuestBerths == portDto.numberOfGuestBerths && Objects.equals(portName, portDto.portName) && Objects.equals(email, portDto.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(portName, email, numberOfGuestBerths);
    }
}
