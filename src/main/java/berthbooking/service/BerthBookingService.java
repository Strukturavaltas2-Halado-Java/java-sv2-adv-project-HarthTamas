package berthbooking.service;

import berthbooking.dtos.*;
import berthbooking.exceptions.BerthNotFoundException;
import berthbooking.exceptions.NumberOfBerthsExceedsLimitException;
import berthbooking.exceptions.PortNotFoundException;
import berthbooking.model.Berth;
import berthbooking.model.Port;
import berthbooking.repository.BerthRepository;
import berthbooking.repository.PortRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class BerthBookingService {

    private PortRepository portRepository;

    private BerthRepository berthRepository;
    private ModelMapper modelMapper;

    public PortDto createPort(CreatePortCommand command) {
        Port port = new Port(command.getPortName(), command.getEmail(), command.getNumberOfGuestBerths());
        portRepository.save(port);
        return modelMapper.map(port, PortDto.class);
    }

    public PortDto getPortById(long id) {
        Port port = portRepository.findById(id).orElseThrow(() -> new PortNotFoundException(id));
        return modelMapper.map(port, PortDto.class);
    }

    public List<PortDto> getPorts(Optional<String> name, Optional<Integer> value) {
        List<Port> ports = portRepository.findAllByOptionalOfNameAndNumberOfBerths(name, value);
        return ports.stream().map(port -> modelMapper.map(port, PortDto.class))
                .collect(Collectors.toList());
    }


    public void deletePortById(long id) {
        if (portRepository.existsById(id)) {
            portRepository.deleteById(id);
        } else {
            throw new PortNotFoundException(id);
        }
    }

    public PortDto updatePortEmailAndNumberOfBerths(long id, UpdatePortCommand command) {
        Port port = portRepository.findById(id).orElseThrow(() -> new PortNotFoundException(id));
        port.setNumberOfGuestBerths(command.getNumberOfGuestBerths());
        port.setEmail(command.getEmail());
        return modelMapper.map(port, PortDto.class);
    }

    public PortDto addBerthToPort(long id, CreateBerthCommand command) {
        Port port = portRepository.findById(id).orElseThrow(() -> new PortNotFoundException(id));
        Berth berth = new Berth(command.getCode(), command.getLength(), command.getWidth(), command.getBerthType());
        if (port.getBerths().size() < port.getNumberOfGuestBerths()) {
            berthRepository.save(berth);
            port.addBerth(berth);
        } else {
            throw new NumberOfBerthsExceedsLimitException(id, port.getNumberOfGuestBerths());
        }
        return modelMapper.map(port, PortDto.class);
    }

    public List<BerthDto> getAllBerthsByPortId(Long portId) {
        if (!portRepository.existsById(portId)) {
            throw new PortNotFoundException(portId);
        }
        List<Berth> berths = berthRepository.findAllByPort_Id(portId);
        return berths.stream().map(berth -> modelMapper.map(berth, BerthDto.class)).collect(Collectors.toList());
    }

    public BerthDto getBerthById(Long berthId) {
        Berth berth = berthRepository.findById(berthId).orElseThrow(() -> new BerthNotFoundException(berthId));
        return modelMapper.map(berth,BerthDto.class);
    }



}
