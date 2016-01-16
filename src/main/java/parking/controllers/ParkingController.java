package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.exceptions.UserException;
import parking.service.ParkingService;

import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<Parking> getAllAvailable() throws UserException {
        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        return parkingService.getAvailable().stream()
                .map(mapper)
                .collect(Collectors.<Parking> toList());

    }

    @RequestMapping(value = "/available", method = RequestMethod.DELETE)
    public void recallParking() {
        parkingService.recallParking();
    }

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