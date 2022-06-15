package berthbooking.controller;

import berthbooking.dtos.CreatePortCommand;
import berthbooking.dtos.PortDto;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.List;

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
    public List<PortDto> getPorts() {
        return service.getPorts();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePortById(@PathVariable() long id) {
        service.deletePortById(id);
    }




}
