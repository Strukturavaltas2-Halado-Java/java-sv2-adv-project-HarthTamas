package berthbooking.controller;

import berthbooking.dtos.BerthDto;
import berthbooking.dtos.CreateBerthCommand;
import berthbooking.model.Berth;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/ports/{portId}")
public class BerthController {

    private BerthBookingService service;

    @PostMapping
    public BerthDto addBerthToPort(@PathVariable("portId") Long portId, @RequestBody CreateBerthCommand command) {
        return service.addBerthToPort(portId, command);
    }

    @GetMapping("/berths")
    public List<BerthDto> getAllBerthsByPortId(@PathVariable("portId") Long portId) {
        return service.getAllBerthsByPortId(portId);
    }


}
