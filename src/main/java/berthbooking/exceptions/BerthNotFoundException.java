package berthbooking.exceptions;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;

public class BerthNotFoundException extends AbstractThrowableProblem {
    public BerthNotFoundException(Long berthId) {
        super(URI.create("/berth-not-found"),
                "Bert not found",
                Status.NOT_FOUND,
                String.format("Bert not found with %d id",berthId));
    }
}
