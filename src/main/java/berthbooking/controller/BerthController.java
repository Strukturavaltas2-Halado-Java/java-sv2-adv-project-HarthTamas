package berthbooking.controller;

import berthbooking.dtos.BerthDto;
import berthbooking.dtos.CreateBerthCommand;
import berthbooking.model.Berth;
import berthbooking.service.BerthBookingService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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






}
