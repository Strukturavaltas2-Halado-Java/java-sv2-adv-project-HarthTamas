package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.time.LocalDate;

public class OutOfActualYearsSeasonException extends AbstractThrowableProblem {
    public OutOfActualYearsSeasonException(LocalDate fromDate) {
        super(URI.create("/berths/requested-date-is-out-of-actual-years-season"),
                "Requested date is out of actual year's season",
                Status.NOT_ACCEPTABLE,
                String.format("Requested date (%s) is out of actual year's season", fromDate));
    }
}
