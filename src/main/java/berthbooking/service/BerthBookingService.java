package berthbooking.service;

import berthbooking.dtos.CreatePortCommand;
import berthbooking.dtos.PortDto;
import berthbooking.model.Port;
import berthbooking.repository.PortRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class BerthBookingService {

    private PortRepository portRepository;
    private ModelMapper modelMapper;

    public PortDto createPort(CreatePortCommand command) {
        Port port = new Port(command.getPortName(), command.getEmail(), command.getNumberOfGuestBerths());
        portRepository.save(port);
        return modelMapper.map(port,PortDto.class);
    }


}
