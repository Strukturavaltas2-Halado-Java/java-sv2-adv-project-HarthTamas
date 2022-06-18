package berthbooking;

import berthbooking.dtos.*;
import berthbooking.model.Berth;
import berthbooking.model.BerthType;
import berthbooking.service.BerthBookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.zalando.problem.Problem;
import org.zalando.problem.violations.ConstraintViolationProblem;
import org.zalando.problem.violations.Violation;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"delete from berth_bookings", "delete from berths", "delete from ports"})
public class PortControllerWebClientIT {


    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BerthBookingService service;

    PortDto portBadacsony;
    PortDto portKeszthely;
    PortDto portSzigliget;


    @BeforeEach
    void init() {
        portBadacsony = service.createPort(new CreatePortCommand("Badacsony", "badacsony@balaport.hu", 8));
        portSzigliget = service.createPort(new CreatePortCommand("Szigliget", "szigliget@balaport.hu", 3));
        portKeszthely = service.createPort(new CreatePortCommand("Keszthely", "keszthely@balaport.hu", 14));
    }

    @Test
    void testCreatePort() {
        PortDto created =
                webTestClient.post()
                        .uri("api/ports")
                        .bodyValue(new CreatePortCommand("Keszthely", "keszthely@balaport.hu", 14))
                        .exchange()
                        .expectStatus().isCreated()
                        .expectBody(PortDto.class)
                        .returnResult().getResponseBody();
        assertThat(created).isEqualTo(portKeszthely);
    }

    @Test
    void testGetPortById() {
        PortDto result = webTestClient.get()
                .uri("api/ports/{id}", portBadacsony.getId())
                .exchange()
                .expectBody(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).isEqualTo(portBadacsony);
    }

    @Test
    void testGetAllPorts() {
        List<PortDto> result = webTestClient.get()
                .uri("api/ports")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(3).contains(portBadacsony, portKeszthely, portSzigliget);
    }

    @Test
    void testGetPortsWithName() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("name", "Badacsony").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).containsOnly(portBadacsony);
    }

    @Test
    void testGetPortsWithMinNumberOfBerths() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("value", 6).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(2).contains(portBadacsony, portKeszthely);
    }

    @Test
    void testGetPortsWithNameAndMinNumberOfBerths() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("value", 6).queryParam("name", "Badacsony").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).contains(portBadacsony);
    }

    @Test
    void testDeleteById() {
        webTestClient.delete()
                .uri("api/ports/{id}", portKeszthely.getId())
                .exchange()
                .expectStatus().isNoContent();

        List<PortDto> result = webTestClient.get()
                .uri("api/ports")
                .exchange()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();

        assertThat(result).hasSize(2).contains(portBadacsony, portSzigliget);
    }

    @Test
    void testUpdateWithNumberOfGuestBerths() {
        PortDto result = webTestClient.put()
                .uri("api/ports/{id}", portSzigliget.getId())
                .bodyValue(new UpdatePortCommand(6))
                .exchange()
                .expectStatus().isOk()
                .expectBody(PortDto.class)
                .returnResult().getResponseBody();

        assertThat(result.getNumberOfGuestBerths()).isEqualTo(6);
    }

    @Test
    void testAddBerthToPort() {
        PortDto result = webTestClient.post()
                .uri(uriBuilder -> uriBuilder.path("api/ports/{id}").build(portSzigliget.getId()))
                .bodyValue(new CreateBerthCommand("Wifi-01", 1_100, 300, BerthType.POWER_WATER_WIFI))
                .exchange()
                .expectBody(PortDto.class)
                .returnResult().getResponseBody();
        List<BerthDto> berthDtos = result.getBerths();
        assertThat(berthDtos)
                .hasSize(1)
                .extracting(BerthDto::getCode)
                .containsOnly("Wifi-01");
    }

    @Test
    void testGetPortByIdWithWrongId() {
        Problem p =
                webTestClient.get()
                        .uri("/api/ports/{id}", 100L)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Port with 100 id not found");
    }

    @Test
    void testDeletePortByIdWithWrongId() {
        Problem p =
                webTestClient.delete()
                        .uri("/api/ports/{id}", 100L)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Port with 100 id not found");
    }

    @Test
    void testUpdatePortWithWrongPortId() {
        Problem p =
                webTestClient.put()
                        .uri("/api/ports/{id}", 100L)
                        .bodyValue(new UpdatePortCommand(6))
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Port with 100 id not found");
    }

    @Test
    void testAddBerthsUpdatePortWithWrongPortId() {
        Problem p =
                webTestClient.put()
                        .uri("/api/ports/{id}", 100L)
                        .bodyValue(new CreateBerthCommand("V-01", 1_100, 300, BerthType.POWER_WATER_WIFI))
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Port with 100 id not found");
    }

    @Test
    void testUpdateWithWrongNumberOfGuestBerths() {
        PortDto portDto = service.addBerthToPort(portBadacsony.getId(),
                new CreateBerthCommand("Wifi - 01", 12_000, 300, BerthType.POWER_WATER_WIFI));
        BerthDto berthDto = portDto.getBerths().get(0);
        service.addBookingToBerthById(berthDto.getId(),
                new CreateBookingCommand("Gyagyás fóka", "H-121212", 730, 240, LocalDate.of(2022, Month.AUGUST, 1), 2));

        Problem p = webTestClient.put()
                .uri("api/ports/{id}", portDto.getId())
                .bodyValue(new UpdatePortCommand(0))
                .exchange()
                .expectBody(Problem.class)
                .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Requested number of guest berths (0) is less than booked berths (1)");
    }

    @Test
    void testCreatePortWithInvalidNumberOfGuestBerths() {
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
    void testAddBerthToPortWithInvalidNumberOfGuestBerths() {
        ConstraintViolationProblem cvp =
                webTestClient.post()
                        .uri(uriBuilder -> uriBuilder.path("api/ports/{id}").build(portSzigliget.getId()))
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


//metdusok tesztelése
//      exceptionok tesztelése
//    validációk tesztelése (constraintek, problem.class-ok)


}
