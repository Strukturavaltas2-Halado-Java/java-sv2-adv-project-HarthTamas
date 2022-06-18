package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import javax.validation.constraints.PositiveOrZero;
import java.net.URI;

public class RequestedNumberOfGuestBerthsIsLessThaneBookedBerthsException extends AbstractThrowableProblem {
    public RequestedNumberOfGuestBerthsIsLessThaneBookedBerthsException(long berthsWithBookings, int requestedNumberOfGuestBerths) {
        super(URI.create("api/ports/requested-numbern-of-guest-berths-is-less-than-booked-berths"),
                "Requested number of guest berths is less than booked berths",
                Status.BAD_REQUEST,
                String.format("Requested number of guest berths (%d) is less than booked berths (%d)",requestedNumberOfGuestBerths,berthsWithBookings));
    }
}
