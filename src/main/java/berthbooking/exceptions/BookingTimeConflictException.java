package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BookingTimeConflictException extends AbstractThrowableProblem {
    public BookingTimeConflictException(Long id) {
        super(URI.create("/berths/requested-booking-time-is-already-taken"),
                "Requested booking time period is already taken",
                Status.BAD_REQUEST,
                String.format("Berth (id: %d) is already taken in the requested time period",id));
    }
}
