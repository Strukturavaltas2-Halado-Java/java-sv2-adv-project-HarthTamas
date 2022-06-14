package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class PortNotFoundException extends AbstractThrowableProblem {
    public PortNotFoundException(long id) {
        super(URI.create("/api/port-not-found"),
                "Port not found",
                Status.NOT_FOUND,
                String.format("Port with %d id not found",id));
    }
}
