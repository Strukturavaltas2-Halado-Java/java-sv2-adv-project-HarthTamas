package berthbooking.service;

import berthbooking.dtos.*;
import berthbooking.exceptions.*;
import berthbooking.model.Berth;
import berthbooking.model.Booking;
import berthbooking.model.Port;
import berthbooking.repository.BerthRepository;
import berthbooking.repository.PortRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class BerthBookingService {

    private static final LocalDate OPENING_DATE = LocalDate.of(LocalDate.now().getYear(), Month.APRIL, 1);
    private static final LocalDate CLOSING_DATE = LocalDate.of(LocalDate.now().getYear(), Month.OCTOBER, 31);

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

    @Transactional
    public PortDto updatePortNumberOfGuestBerths(Long id, UpdatePortCommand command) {
        Port port = portRepository.findById(id).orElseThrow(() -> new PortNotFoundException(id));
        long berthsWithBookings = port.getBerths().stream().filter(berth -> berth.getBookings().size() > 0).count();
        if (command.getNumberOfGuestBerths() >= berthsWithBookings) {
            port.setNumberOfGuestBerths(command.getNumberOfGuestBerths());
        } else {
            throw  new RequestedNumberOfGuestBerthsIsLessThaneBookedBerthsException(berthsWithBookings, command.getNumberOfGuestBerths());
        }
        return modelMapper.map(port, PortDto.class);
    }


    @Transactional
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

    public List<BerthDto> getAllBerths(Optional<Integer> width, Optional<String> portName) {
        List<Berth> berths = berthRepository.findAllBerthsByPortIdAndWidth(width, portName);
        berths.stream().forEach(berth -> sortBookings(berth));
        return berths.stream().map(berth -> modelMapper.map(berth, BerthDto.class)).collect(Collectors.toList());
    }

    public BerthDto getBerthById(Long berthId) {
        Berth berth = berthRepository.findById(berthId).orElseThrow(() -> new BerthNotFoundException(berthId));
        sortBookings(berth);
        return modelMapper.map(berth, BerthDto.class);
    }

    public void deleteBerthById(long id) {
        if (berthRepository.existsById(id)) {
            berthRepository.deleteById(id);
        } else {
            throw new BerthNotFoundException(id);
        }
    }

    @Transactional
    public BerthDto updateBerthById(long id, UpdateBerthCommand command) {
        Berth berth = berthRepository.findById(id).orElseThrow(() -> new BerthNotFoundException(id));
        berth.setCode(command.getCode());
        berth.setLength(command.getLength());
        berth.setWidth(command.getWidth());
        berth.setBerthType(command.getBerthType());
        return modelMapper.map(berth, BerthDto.class);
    }

    @Transactional
    public BerthDto addBookingToBerthById(long id, CreateBookingCommand command) {
        Berth berth = berthRepository.findById(id).orElseThrow(() -> new BerthNotFoundException(id));
        checkConditions(berth, command);
        Booking booking = new Booking(command.getBoatName(), command.getRegistrationNumber(),
                command.getBoatLength(), command.getBoatWidth(), command.getFromDate(), command.getNumberOfDays());
        booking.setTimeOfBooking(LocalDateTime.now());
        berth.addBooking(booking);
        return modelMapper.map(berth, BerthDto.class);
    }

    private void checkConditions(Berth berth, CreateBookingCommand command) {
        isBookingInActualYearsSeason(command);
        isBoatSmallerThanBerth(berth, command);
        isBookingTimeFree(berth, command);
    }

    private void isBoatSmallerThanBerth(Berth berth, CreateBookingCommand command) {
        if (command.getBoatWidth() >= berth.getWidth() || command.getBoatLength() >= berth.getLength()) {
            throw new BoatSizeException(berth.getId());
        }
    }

    private void isBookingTimeFree(Berth berth, CreateBookingCommand command) {
        LocalDate startDate = command.getFromDate();
        LocalDate endDate = command.getFromDate().plusDays(command.getNumberOfDays());
        for (Booking actual : berth.getBookings()) {
            LocalDate bookedStartDay = actual.getFromDate();
            LocalDate bookedEndDay = actual.getFromDate().plusDays(actual.getNumberOfDays());
            if (endDate.isAfter(bookedStartDay) && startDate.isBefore(bookedEndDay)) {
                throw new BookingTimeConflictException(berth.getId());
            }
        }
    }

    private void isBookingInActualYearsSeason(CreateBookingCommand command) {
        if (command.getFromDate().isBefore(OPENING_DATE) || command.getFromDate().isAfter(CLOSING_DATE.minusDays(command.getNumberOfDays() - 1))) {
            throw new OutOfActualYearsSeasonException(command.getFromDate());
        }
    }

    private void sortBookings(Berth berth) {
        List<Booking> bookingsOrdered = berth.getBookings().stream().sorted(Comparator.comparing(Booking::getFromDate)).toList();
        berth.setBookings(bookingsOrdered);
    }


}
