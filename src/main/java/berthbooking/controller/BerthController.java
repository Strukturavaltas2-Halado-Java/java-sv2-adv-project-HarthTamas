package berthbooking.controller;

import berthbooking.dtos.BerthDto;
import berthbooking.dtos.CreateBookingCommand;
import berthbooking.dtos.UpdateBerthCommand;
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

@AllArgsConstructor
@RestController
@RequestMapping("api/berths")
@Tag(name = "Operations on berths")
public class BerthController {

    private BerthBookingService service;


    @GetMapping()
    @Operation(summary = "Find berths", description = "Find berths")
    public List<BerthDto> getAllBerthsWithParameters(@Parameter(description = "Width of the berth", example = "240") @RequestParam Optional<Integer> width,
                                                     @Parameter(description = "Name of the town where port is located", example = "Keszthely")@RequestParam Optional<String> town) {
        return service.getAllBerthsWithParameters(width, town);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Find berth by id", description = "Find berth by id.")
    @ApiResponse(responseCode = "404", description = "Berth not found")
    public BerthDto getBerthById(@Parameter(description = "Id of the berth", example = "1") @PathVariable("id") long berthId) {
        return service.getBerthById(berthId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete berth by id", description = "Delete berth by id.")
    @ApiResponse(responseCode = "404", description = "Berth not found")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBerthById(@Parameter(description = "Id of the berth", example = "1") @PathVariable("id") long id) {
        service.deleteBerthById(id);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update berth by id", description = "Update berth by id.")
    @ApiResponse(responseCode = "404", description = "Berth not found")
    public BerthDto updateBerthById(@Parameter(description = "Id of the berth", example = "1") @PathVariable("id") long id, @Valid @RequestBody UpdateBerthCommand command) {
        return service.updateBerthById(id, command);
    }

    @PostMapping("/{id}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Creates a booking")
    @ApiResponse(responseCode = "201", description = "Booking has been created")
    public BerthDto addBookingToBerthById(@Parameter(description = "Id of the berth", example = "1") @PathVariable("id") long id, @Valid @RequestBody CreateBookingCommand command) {
        return service.addBookingToBerthById(id, command);
    }


}
