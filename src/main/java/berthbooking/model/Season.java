package berthbooking.model;

import java.time.LocalDate;
import java.time.Month;

public class Season {

    private LocalDate startDate;
    private LocalDate endDate;

    public Season(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Season() {
    }

    public LocalDate getStartDate() {
        return startDate == null ? LocalDate.of(LocalDate.now().getYear(), Month.APRIL, 1) : startDate;
    }

    public LocalDate getEndDate() {
        return endDate == null ? LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 31) : endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
