package berthbooking.dtos;

import berthbooking.model.BerthType;
import berthbooking.model.Booking;
import berthbooking.model.Port;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BerthDto {

    private Long id;
    private String code;
    private int length;
    private int width;
    private BerthType berthType;
    private List<Booking> bookings = new ArrayList<>();

}
