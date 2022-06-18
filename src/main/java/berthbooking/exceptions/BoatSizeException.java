package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BoatSizeException extends AbstractThrowableProblem {
    public BoatSizeException(long id) {
        super(URI.create("/berths/berth-size-is-too-small-for-boat"),
                "Berth size is too small for this boat",
                Status.BAD_REQUEST,
                String.format("Berth (id: %d) is too small for this boat", id));
    }
}
