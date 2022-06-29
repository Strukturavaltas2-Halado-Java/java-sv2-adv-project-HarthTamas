package berthbooking;

import berthbooking.dtos.*;
import berthbooking.model.BerthType;
import berthbooking.service.BerthBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"delete from berth_bookings", "delete from berths", "delete from ports"})
public class ValidationWebClientIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BerthBookingService service;

    PortDto portBadacsony;

    PortDto portSzigliget;

    BerthDto berthBadaPW;

    @BeforeEach
    void init() {
        portBadacsony = service.createPort(new CreatePortCommand("Badacsony", "badacsony@balaport.hu", 8));
        portSzigliget = service.createPort(new CreatePortCommand("Szigliget", "szigliget@balaport.hu", 2));
        berthBadaPW = service.addBerthToPort(portBadacsony.getId(),
                new CreateBerthCommand("PW-02", 1_100, 290, BerthType.POWER_WATER)).getBerths().get(0);
    }

    @Test
    void testCreatePortWithInvalidValues() {
        ConstraintViolationProblem cvp =
                webTestClient.post()
                        .uri("/api/ports")
                        .bodyValue(new CreatePortCommand("", "bala@.ss", -2))
                        .exchange()
                        .expectBody(ConstraintViolationProblem.class)
                        .returnResult().getResponseBody();

        assertThat(cvp.getViolations())
                .extracting(Violation::getMessage)
                .contains("The number of guest berths must be positive or zero!")
                .contains("Must be a valid email format!")
                .contains("Ports must have a name!");
    }

    @Test
    void testAddBerthToPortWithInvalidValues() {
        ConstraintViolationProblem cvp =
                webTestClient.post()
                        .uri(uriBuilder -> uriBuilder.path("api/ports/{id}/berths").build(portSzigliget.getId()))
                        .bodyValue(new CreateBerthCommand("", 0, -1, BerthType.POWER_WATER_WIFI))
                        .exchange()
                        .expectBody(ConstraintViolationProblem.class)
                        .returnResult().getResponseBody();

        assertThat(cvp.getViolations())
                .extracting(Violation::getMessage)
                .contains("Berths must have a code!")
                .contains("The length of the berth must be positive number!")
                .contains("The width of the berth must be positive number!");
    }

    @Test
    void testAddBookingWithInvalidValues() {
        ConstraintViolationProblem cvp =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthBadaPW.getId())
                        .bodyValue(new CreateBookingCommand("", "", 0, 0, LocalDate.of(2019, Month.AUGUST, 1), 8))
                        .exchange()
                        .expectBody(ConstraintViolationProblem.class)
                        .returnResult().getResponseBody();

        assertThat(cvp.getViolations())
                .extracting(Violation::getMessage)
                .contains("Boats must have a name!")
                .contains("Boats must have registration number!")
                .contains("The length of the boat must be positive number!")
                .contains("The width of the boat must be positive number!")
                .contains("This date must be in the present or in the future!")
                .contains("Must be a positive number and equal or less than 3");
    }

}
