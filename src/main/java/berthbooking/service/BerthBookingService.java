package berthbooking.service;

import berthbooking.dtos.CreatePortCommand;
import berthbooking.dtos.PortDto;
import berthbooking.exceptions.PortNotFoundException;
import berthbooking.model.Port;
import berthbooking.repository.PortRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    public PortDto getPortById(long id) {
        Port port = portRepository.findById(id).orElseThrow(()->new PortNotFoundException(id));
        return modelMapper.map(port,PortDto.class);
    }

    public List<PortDto> getPorts() {
        return portRepository.findAll().stream().map(port -> modelMapper.map(port,PortDto.class)).collect(Collectors.toList());
    }

    public void deletePortById(long id) {
        if (portRepository.existsById(id)) {
            portRepository.deleteById(id);
        } else {
            throw new PortNotFoundException(id);
        }
    }
}
