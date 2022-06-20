package berthbooking.controller;

import berthbooking.dtos.*;
import berthbooking.service.BerthBookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/ports")
@Tag(name = "Operations on ports")
public class PortController {

    private BerthBookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a port")
    @ApiResponse(responseCode = "201", description = "Port has been created")
    public PortDto createPort(@Valid @RequestBody CreatePortCommand command) {
        return service.createPort(command);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find port by id")
    @ApiResponse(responseCode = "404", description = "Port not found")
    public PortDto getPortById(@Parameter(description = "Id of the port", example = "1") @PathVariable() long id) {
        return service.getPortById(id);
    }

    @GetMapping
    @Operation(summary = "Find ports")
    @ApiResponse(responseCode = "404", description = "Ports not found")
    public List<PortDto> getPorts(@Parameter(description = "Name of the town where port is located", example = "Keszthely") @RequestParam Optional<String> town,
                                  @Parameter(description = "Number of minimum guest berths", example = "1") @RequestParam Optional<Integer> capacity) {
        return service.getPorts(town, capacity);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete port by id")
    @ApiResponse(responseCode = "404", description = "Port not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortById(@Parameter(description = "Id of the port", example = "1") @PathVariable() long id) {
        service.deletePortById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update port by id")
    @ApiResponse(responseCode = "404", description = "Port not found")
    public PortDto updatePortNumberOfGuestBerths(@Parameter(description = "Id of the port", example = "1") @PathVariable("id") Long id, @Valid @RequestBody UpdatePortCommand command) {
        return service.updatePortNumberOfGuestBerths(id, command);
    }

    @PostMapping("/{id}/berths")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a Berth")
    @ApiResponse(responseCode = "201", description = "Berth has been created")
    public PortDto addBerthToPort(@Parameter(description = "Id of the port", example = "1") @PathVariable("id") Long id, @Valid @RequestBody CreateBerthCommand command) {
        return service.addBerthToPort(id, command);
    }

}
