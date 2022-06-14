package berthbooking.controller;

import berthbooking.dtos.CreatePortCommand;
import berthbooking.dtos.PortDto;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("api/ports")
public class PortController {

    private BerthBookingService service;

    @PostMapping
    public PortDto createPort(@RequestBody CreatePortCommand command) {
        return service.createPort(command);
    }





}
