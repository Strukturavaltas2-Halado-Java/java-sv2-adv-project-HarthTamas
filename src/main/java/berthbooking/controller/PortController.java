package berthbooking.controller;

import berthbooking.dtos.*;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/ports")
public class PortController {

    private BerthBookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortDto createPort(@Valid @RequestBody CreatePortCommand command) {
        return service.createPort(command);
    }

    @GetMapping("/{id}")
    public PortDto getPortById(@PathVariable() long id) {
        return service.getPortById(id);
    }

    @GetMapping
    public List<PortDto> getPorts(@RequestParam Optional<String> name, @RequestParam Optional<Integer> capacity) {
        return service.getPorts(name, capacity);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortById(@PathVariable() long id) {
        service.deletePortById(id);
    }

    @PutMapping("/{id}")
    public PortDto updatePortNumberOfGuestBerths(@PathVariable("id") Long id, @Valid @RequestBody UpdatePortCommand command) {
        return service.updatePortNumberOfGuestBerths(id, command);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public PortDto addBerthToPort(@PathVariable("id") Long id, @Valid @RequestBody CreateBerthCommand command) {
        return service.addBerthToPort(id, command);
    }


}
