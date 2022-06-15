package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class NumberOfBerthsExceedsLimitException extends AbstractThrowableProblem {
    public NumberOfBerthsExceedsLimitException(Long portId, int numberOfGuestBerths) {
        super(URI.create("/api/ports/berths-exceed-limit"),
                "Number of berths exceeds limit",
                Status.NOT_ACCEPTABLE,
                String.format("Number of berths exceeds limit(%d) at port with id: %d",numberOfGuestBerths,portId));
    }
}
