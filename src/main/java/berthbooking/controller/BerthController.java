package berthbooking.controller;

import berthbooking.dtos.BerthDto;
import berthbooking.dtos.BookingCommand;
import berthbooking.dtos.UpdateBerthCommand;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/berths")
public class BerthController {

    private BerthBookingService service;


    @GetMapping()
    public List<BerthDto> getAllBerths() {
        return service.getAllBerths();
    }

    @GetMapping("/{id}")
    public BerthDto getBerthById(@PathVariable("id") long berthId) {
        return service.getBerthById(berthId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBethById(@PathVariable("id") long id) {
        service.deleteBerthById(id);
    }

    @PutMapping("/{id}")
    public BerthDto updateBerthById(@PathVariable("id") long id, @RequestBody UpdateBerthCommand command) {
        return service.updateBerthById(id, command);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public BerthDto addBookingToBerthById(@PathVariable("id") long id, @RequestBody BookingCommand command) {
        return service.addBookingToBerthById(id, command);
    }






}
