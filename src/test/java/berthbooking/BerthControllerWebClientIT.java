package berthbooking;

import berthbooking.dtos.*;
import berthbooking.model.BerthType;
import berthbooking.model.Booking;
import berthbooking.model.Season;
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
import static org.assertj.core.groups.Tuple.tuple;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(statements = {"delete from berth_bookings", "delete from berths", "delete from ports"})
public class BerthControllerWebClientIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    BerthBookingService service;

    int testYear;
    PortDto portBadacsony;
    PortDto portKeszthely;
    PortDto portSzigliget;
    BerthDto berthBadaWifi;
    BerthDto berthBadaPW;
    BerthDto berthKesztBase;
    BerthDto berthSzigPW;

    @BeforeEach
    void init() {
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

        testYear = LocalDate.now().getYear() + 1;
        Season testSeason = new Season(LocalDate.of(testYear, Month.APRIL, 1), LocalDate.of(testYear, Month.OCTOBER, 31));
        service.setSeason(testSeason);
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
        assertThat(result).hasSize(3).contains(berthBadaPW, berthBadaWifi, berthSzigPW);
    }

    @Test
    void testGetBerthsWithPortName() {
        List<BerthDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/berths").queryParam("town", "Szigliget").build())
                .exchange()
                .expectBodyList(BerthDto.class)
                .returnResult().getResponseBody();
        assertThat(result).hasSize(1).contains(berthSzigPW);
    }

    @Test
    void testGetBerthsWithPortNameAndWidth() {
        List<BerthDto> result = webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("api/berths")
                        .queryParam("town", "Badacsony")
                        .queryParam("width", 295)
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
                .bodyValue(new CreateBookingCommand("Gyagyás fóka", "H-121212",
                        730, 240, LocalDate.of(testYear, Month.JULY, 1), 2))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(BerthDto.class)
                .returnResult().getResponseBody();

        List<Booking> bookings = result.getBookings();
        assertThat(bookings)
                .hasSize(1)
                .extracting(Booking::getFromDate, Booking::getNumberOfDays)
                .contains(tuple(LocalDate.of(testYear, Month.JULY, 1), 2));
    }

    @Test
    void testGetBerthByIdWithWrongId() {
        Problem p =
                webTestClient.get()
                        .uri("/api/berths/{id}", 0L)
                        .exchange()
                        .expectStatus().isNotFound()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth not found with 0 id");
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
    void testAddBookingWithWrongBoatSize() {
        Problem p =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                        .bodyValue(new CreateBookingCommand("Szélvész", "H-2342342", 880, 277,
                                LocalDate.of(testYear, Month.AUGUST, 1), 2))
                        .exchange()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth (id: " + berthKesztBase.getId() + ") is too small for this boat");
    }

    @Test
    void testAddBookingWithOutOfSeasonDate() {
        LocalDate outOfSeasonDate = LocalDate.of(testYear, Month.DECEMBER, 1);
        Problem p =
                webTestClient.post()
                        .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                        .bodyValue(new CreateBookingCommand("Flyer", "H-2342342", 720, 250,
                                outOfSeasonDate, 2))
                        .exchange()
                        .expectBody(Problem.class)
                        .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Requested date (" + outOfSeasonDate + ") is out of season");
    }

    @Test
    void testAddBookingWithBookedTimePeriod() {
        webTestClient.post()
                .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                .bodyValue(new CreateBookingCommand("Gyagyás fóka", "H-121212", 730, 240,
                        LocalDate.of(testYear, Month.AUGUST, 1), 3))
                .exchange()
                .expectStatus().isCreated();

        Problem p = webTestClient.post()
                .uri("api/berths/{id}/bookings", berthKesztBase.getId())
                .bodyValue(new CreateBookingCommand("Flyer", "H-2342342", 720, 250,
                        LocalDate.of(testYear, Month.AUGUST, 2), 2))
                .exchange()
                .expectBody(Problem.class)
                .returnResult().getResponseBody();
        assertThat(p.getDetail()).isEqualTo("Berth (id: " + berthKesztBase.getId() + ") is already booked in the requested time period");
    }

}
