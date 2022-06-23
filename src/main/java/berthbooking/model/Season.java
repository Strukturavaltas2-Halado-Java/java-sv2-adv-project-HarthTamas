package berthbooking.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Month;

@AllArgsConstructor
@NoArgsConstructor
public class Season {

    private LocalDate startDate;
    private LocalDate endDate;

    public LocalDate getStartDate() {
        return startDate == null ? LocalDate.of(LocalDate.now().getYear(), Month.APRIL, 1) : startDate;
    }

    public LocalDate getEndDate() {
        return endDate == null ? LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 31) : endDate;
    }

}
