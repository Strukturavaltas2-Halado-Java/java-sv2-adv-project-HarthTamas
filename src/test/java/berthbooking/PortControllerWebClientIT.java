package berthbooking;

import berthbooking.dtos.*;
import berthbooking.model.BerthType;
import berthbooking.model.Booking;
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

import java.time.*;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;


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

    BerthDto berthBadaWifi;
    BerthDto berthBadaPW;
    BerthDto berthKesztBase;
    BerthDto berthSzigPW;

    @BeforeEach
    void init() {
        Clock clock = Clock.fixed(Instant.parse("2022-04-01T00:00:00.00Z"), ZoneId.of("UTC"));
        LocalDate date = LocalDate.now(clock);

        portSzemes = service.createPort(new CreatePortCommand("Balatonszemes", "balatonszemes@balaport.hu", 4));
        portBadacsony = service.createPort(new CreatePortCommand("Badacsony", "badacsony@balaport.hu", 8));
        portSzigliget = service.createPort(new CreatePortCommand("Szigliget", "szigliget@balaport.hu", 2));
        portKeszthely = service.createPort(new CreatePortCommand("Keszthely", "keszthely@balaport.hu", 14));
        berthBadaWifi = service.addBerthToPort(portBadacsony.getId(),
                new CreateBerthCommand("Wifi-01", 1_100, 300, BerthType.POWER_WATER_WIFI)).getBerths().get(0);
        berthBadaPW = service.addBerthToPort(portBadacsony.getId(),
                new CreateBerthCommand("PW-02", 1_100, 290, BerthType.POWER_WATER)).getBerths().get(1);
        berthKesztBase = service.addBerthToPort(portKeszthely.getId(),
                new CreateBerthCommand("Base-02", 1_000, 260, BerthType.BASE)).getBerths().get(0);
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
                .uri(uriBuilder -> uriBuilder.path("api/ports/{id}").build(portSzemes.getId()))
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

    @Test
    void testGetBerthById() {
        BerthDto result = webTestClient.get()
                .uri("api/berths/{id}", berthBadaWifi.getId())
                .exchange()
                .expectBody(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).isEqualTo(berthBadaWifi);
    }

    @Test
    void testGetAllBerths() {
        List<BerthDto> result = webTestClient.get()
                .uri("api/berths")
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();

        assertThat(result)
                .contains(berthBadaWifi, berthBadaPW, berthKesztBase, berthSzigPW);
    }

    @Test
    void testGetBerthsWithMinWidth() {
        List<BerthDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/berths").queryParam("width", 265).build())
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(3).contains(berthBadaPW, berthBadaWifi,berthSzigPW);
    }

    @Test
    void testGetBerthsWithPortName() {
        List<BerthDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/berths").queryParam("portName","Szigliget").build())
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).contains(berthSzigPW);
    }

    @Test
    void testGetBerthsWithPortNameAndWidth() {
        List<BerthDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/berths")
                        .queryParam("portName","Badacsony")
                        .queryParam("width",295)
                        .build())
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).contains(berthBadaWifi);
    }


    @Test
    void testDeleteBerthById() {
        webTestClient.delete()
                .uri("api/berths/{id}", berthBadaPW.getId())
                .exchange()
                .expectStatus().isNoContent();

        List<BerthDto> result = webTestClient.get()
                .uri("api/berths")
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(3)
                .containsOnly(berthBadaWifi, berthKesztBase, berthSzigPW)
                .doesNotContain(berthBadaPW);
    }

    @Test
    void testUpdateBerthById() {
        BerthDto result = webTestClient.put()
                .uri("api/berths/{id}", berthBadaPW.getId())
                .bodyValue(new UpdateBerthCommand("Wifi-02", 1_100, 290, BerthType.POWER_WATER_WIFI))
                .exchange()
                .expectBody(BerthDto.class)
                .returnResult().getResponseBody();

        assertThat(result.getCode()).isEqualTo("Wifi-02");
    }

    @Test
    void testAddBookingsToBerthById() {
        BerthDto result = webTestClient.post()
                .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                .bodyValue(new CreateBookingCommand("Gyagyás fóka", "H-121212", 730, 240, LocalDate.of(2022, Month.JULY, 1), 2))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BerthDto.class)
                .returnResult().getResponseBody();

        List<Booking> bookings = result.getBookings();
        assertThat(bookings)
                .hasSize(1)
                .extracting(Booking::getFromDate, Booking::getNumberOfDays)
                .contains(tuple(LocalDate.of(2022, Month.JULY, 1), 2));
    }

    @Test
    void testGetBerthByIdWithWrongId() {
        Problem p =
                webTestClient.get()
                        .uri("/api/berths/{id}", 100L)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth not found with 100 id");
    }

    @Test
    void testDeleteBerthByIdWithWrongId() {
        Problem p =
                webTestClient.delete()
                        .uri("/api/berths/{id}", 100L)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth not found with 100 id");
    }

    @Test
    void testAddTooManyBerthsToPort() {
        webTestClient.post()
                .uri("api/ports/{id}", portSzigliget.getId())
                .bodyValue(new CreateBerthCommand("PW-02", 1_200, 350, BerthType.POWER_WATER))
                .exchange()
                .expectStatus().isCreated();

        Problem p = webTestClient.post()
                .uri("api/ports/{id}", portSzigliget.getId())
                .bodyValue(new CreateBerthCommand("PW-04", 1_100, 290, BerthType.POWER_WATER_WIFI))
                .exchange()
                .expectBody(Problem.class)
                .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Number of berths exceeds limit(2) at port with id: " + portSzigliget.getId());
    }

    @Test
    void testAddBookingWithInvalidValues() {
        ConstraintViolationProblem cvp =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthBadaPW.getId())
                        .bodyValue(new CreateBookingCommand("", "", 0, 0, LocalDate.of(2021, Month.AUGUST, 1), 8))
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
                .contains("Must be a positive number and less than 3");
    }

    @Test
    void testAddBookingWithWrongBoatSize() {
        Problem p =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                        .bodyValue(new CreateBookingCommand("Szélvész", "H-2342342", 880, 277, LocalDate.of(2022, Month.AUGUST, 1), 2))
                        .exchange()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth (id: " + berthKesztBase.getId() + ") is too small for this boat");
    }

    @Test
    void testAddBookingWithOutOfSeasonDate() {
        LocalDate date = LocalDate.of(2022, Month.DECEMBER, 1);
        Problem p =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                        .bodyValue(new CreateBookingCommand("Flyer", "H-2342342", 720, 250, date, 2))
                        .exchange()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Requested date ("+date+") is out of actual year's season");
    }

    @Test
    void testAddBookingWithBookedTimePeriod() {
        webTestClient.post()
                .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                .bodyValue(new CreateBookingCommand("Gyagyás fóka", "H-121212", 730, 240, LocalDate.of(2022, Month.AUGUST, 1), 3))
                .exchange()
                .expectStatus().isCreated();

        Problem p = webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                        .bodyValue(new CreateBookingCommand("Flyer", "H-2342342", 720, 250, LocalDate.of(2022,Month.AUGUST,2), 2))
                        .exchange()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth (id: "+berthKesztBase.getId()+") is already booked in the requested time period");
    }

    @Test
    public void givenFixedClock_whenNow_thenGetFixedLocalDateTime() {
        System.out.println(LocalDate.now());
        Clock clock = Clock.fixed(Instant.parse("2022-04-01T00:00:00.00Z"), ZoneId.of("UTC"));

        LocalDate date = LocalDate.now(clock);
        System.out.println(date);
    }

}

