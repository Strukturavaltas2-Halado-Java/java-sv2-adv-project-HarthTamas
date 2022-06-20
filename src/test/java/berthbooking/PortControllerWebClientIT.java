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
import org.zalando.problem.Problem;


import java.time.*;
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
    PortDto portSzemes;
    BerthDto berthSzigPW;

    @BeforeEach
    void init() {
        Clock clock = Clock.fixed(Instant.parse("2022-04-01T00:00:00.00Z"), ZoneId.of("UTC"));
        LocalDate date = LocalDate.now(clock);

        portSzemes = service.createPort(new CreatePortCommand("Balatonszemes", "balatonszemes@balaport.hu", 4));
        portBadacsony = service.createPort(new CreatePortCommand("Badacsony", "badacsony@balaport.hu", 8));
        portSzigliget = service.createPort(new CreatePortCommand("Szigliget", "szigliget@balaport.hu", 2));
        portKeszthely = service.createPort(new CreatePortCommand("Keszthely", "keszthely@balaport.hu", 14));
        berthSzigPW = service.addBerthToPort(portSzigliget.getId(),
                new CreateBerthCommand("PW-01", 1_000, 280, BerthType.POWER_WATER)).getBerths().get(0);
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
        assertThat(result).hasSize(4).contains(portBadacsony, portKeszthely, portSzigliget, portSzemes);
    }

    @Test
    void testGetPortsWithName() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("town", "Badacsony").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).containsOnly(portBadacsony);
    }

    @Test
    void testGetPortsWithMinNumberOfBerths() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("capacity", 6).build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(2).contains(portBadacsony, portKeszthely);
    }

    @Test
    void testGetPortsWithNameAndMinNumberOfBerths() {
        List<PortDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/ports").queryParam("capacity", 6).queryParam("town", "Badacsony").build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).contains(portBadacsony);
    }

    @Test
    void testDeletePortById() {
        webTestClient.delete()
                .uri("api/ports/{id}", portKeszthely.getId())
                .exchange()
                .expectStatus().isNoContent();

        List<PortDto> result = webTestClient.get()
                .uri("api/ports")
                .exchange()
                .expectBodyList(PortDto.class)
                .returnResult().getResponseBody();

        assertThat(result).hasSize(3).contains(portBadacsony, portSzigliget, portSzemes);
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
                .uri(uriBuilder -> uriBuilder.path("api/ports/{id}/berths").build(portSzemes.getId()))
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
    void testAddTooManyBerthsToPort() {
        webTestClient.post()
                .uri("api/ports/{id}/berths", portSzigliget.getId())
                .bodyValue(new CreateBerthCommand("PW-02", 1_200, 350, BerthType.POWER_WATER))
                .exchange()
                .expectStatus().isCreated();

        Problem p = webTestClient.post()
                .uri("api/ports/{id}/berths", portSzigliget.getId())
                .bodyValue(new CreateBerthCommand("PW-04", 1_100, 290, BerthType.POWER_WATER_WIFI))
                .exchange()
                .expectBody(Problem.class)
                .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Number of berths exceeds limit(2) at port with id: " + portSzigliget.getId());
    }


}

