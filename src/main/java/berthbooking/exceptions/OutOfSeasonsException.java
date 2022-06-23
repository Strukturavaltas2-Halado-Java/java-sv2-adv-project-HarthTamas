package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDate;

public class OutOfSeasonsException extends AbstractThrowableProblem {
    public OutOfSeasonsException(LocalDate fromDate) {
        super(URI.create("/berths/requested-date-is-out-of-season"),
                "Requested date is out of season",
                Status.BAD_REQUEST,
                String.format("Requested date (%s) is out of season", fromDate));
    }
}
