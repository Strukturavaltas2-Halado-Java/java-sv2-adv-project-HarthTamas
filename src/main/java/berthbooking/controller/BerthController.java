package berthbooking.controller;

import berthbooking.dtos.BerthDto;
import berthbooking.dtos.CreateBookingCommand;
import berthbooking.dtos.UpdateBerthCommand;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@RestController
@RequestMapping("api/berths")
public class BerthController {

    private BerthBookingService service;


    @GetMapping()
    public List<BerthDto> getAllBerthsWithParameters(@RequestParam Optional<Integer> width, @RequestParam Optional<String> portName) {
        return service.getAllBerthsWithParameters(width, portName);
    }

    @GetMapping("/{id}")
    public BerthDto getBerthById(@PathVariable("id") long berthId) {
        return service.getBerthById(berthId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBerthById(@PathVariable("id") long id) {
        service.deleteBerthById(id);
    }

    @PutMapping("/{id}")
    public BerthDto updateBerthById(@PathVariable("id") long id, @Valid @RequestBody UpdateBerthCommand command) {
        return service.updateBerthById(id, command);
    }

    @PostMapping("/{id}/bookings")
    @ResponseStatus(HttpStatus.CREATED)
    public BerthDto addBookingToBerthById(@PathVariable("id") long id, @Valid @RequestBody CreateBookingCommand command) {
        return service.addBookingToBerthById(id, command);
    }


}
