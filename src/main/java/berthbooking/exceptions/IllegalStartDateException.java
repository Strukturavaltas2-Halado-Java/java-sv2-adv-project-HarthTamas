package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDate;

public class IllegalStartDateException extends AbstractThrowableProblem {
    public IllegalStartDateException(LocalDate fromDate) {
        super(URI.create("/berths/requested-booking-date-starts-in-the-past"),
                "Requested booking date starts in the past",
                Status.NOT_ACCEPTABLE,
                String.format("Requested booking date (%s) starts in the past", fromDate));
    }
}

