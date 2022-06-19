package berthbooking.dtos;

import berthbooking.model.BerthType;
import berthbooking.model.Booking;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public BerthDto(String code, int length, int width, BerthType berthType) {
        this.code = code;
        this.length = length;
        this.width = width;
        this.berthType = berthType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BerthDto berthDto = (BerthDto) o;
        return length == berthDto.length && width == berthDto.width && Objects.equals(code, berthDto.code) && berthType == berthDto.berthType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, length, width, berthType);
    }

}
