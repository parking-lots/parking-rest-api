package parking.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.beans.response.ParkingLot;
import parking.exceptions.UserException;
import parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<Parking> getAllAvailable() throws UserException {
        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot);
        return parkingService.getAvailable().stream()
                .map(mapper)
                .collect(Collectors.<Parking> toList());

    }

    @PreAuthorize("hasRole('ROLE_CAN_SHARE_PARKING')")
    @RequestMapping(value = "/available", method = RequestMethod.DELETE)
    public void recallParking() {
        parkingService.recallParking();
    }

    @PreAuthorize("hasRole('CAN_SHARE_PARKING')")
    @RequestMapping(value = "/available", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody SetUnusedRequest request) {
        parkingService.freeOwnersParking(request);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody ParkingNumberRequest request) throws UserException {
        parkingService.reserve(request);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.DELETE)
    public void cancelRezervation() throws UserException {
        parkingService.cancelRezervation();
    }
}