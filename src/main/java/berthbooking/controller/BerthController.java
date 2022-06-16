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
@RequestMapping("api/berths")
public class BerthController {

    private BerthBookingService service;


    @GetMapping()
    public List<BerthDto> getAllBerths(@PathVariable("portId") Long portId) {
        return service.getAllBerthsByPortId(portId);
    }

    @GetMapping("/{berthId}")
    public BerthDto getBerthById(@PathVariable("berthId") Long berthId) {
        return service.getBerthById(berthId);
    }






}
