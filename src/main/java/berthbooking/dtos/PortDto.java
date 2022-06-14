package berthbooking.dtos;

import berthbooking.model.Berth;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PortDto {

    private Long id;
    private String portName;
    private String email;
    private int numberOfGuestBerths;
    private List<Berth> berths = new ArrayList<>();
}
