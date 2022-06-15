package berthbooking.controller;

import berthbooking.dtos.CreatePortCommand;
import berthbooking.dtos.PortDto;
import berthbooking.dtos.UpdatePortCommand;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("api/ports")
public class PortController {

    private BerthBookingService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PortDto createPort(@RequestBody CreatePortCommand command) {
        return service.createPort(command);
    }

    @GetMapping("/{id}")
    public PortDto getPortById(@PathVariable() long id) {
        return service.getPortById(id);
    }

    @GetMapping
    public List<PortDto> getPorts(@RequestParam Optional<String> name, @RequestParam Optional<Integer> value) {
        return service.getPorts(name,value);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortById(@PathVariable() long id) {
        service.deletePortById(id);
    }

    @PutMapping("/{id}")
    public PortDto updatePortEmailAndNumberOfBerths(@PathVariable("id") Long id, @RequestBody UpdatePortCommand command) {
        return service.updatePortEmailAndNumberOfBerths(id, command);
    }
}
