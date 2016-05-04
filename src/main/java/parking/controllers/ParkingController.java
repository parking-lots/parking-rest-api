package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallSingleParking;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.exceptions.ApplicationException;
import parking.service.ParkingService;

import javax.servlet.http.HttpServletRequest;
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
    public List<Parking> getAllAvailable(HttpServletRequest request) throws ApplicationException {
        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        return parkingService.getAvailable(request).stream()
                .map(mapper)
                .collect(Collectors.<Parking>toList());
    }

    @RequestMapping(value = "/available", method = RequestMethod.DELETE)
    public void recallSingleParking(@Valid @RequestBody RecallSingleParking recallSingleParking) {
        parkingService.recallParking(recallSingleParking);
    }

    @RequestMapping(value = "/available", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody SetUnusedRequest request, HttpServletRequest httpRequest) throws ApplicationException {
        parkingService.freeOwnersParking(request, httpRequest);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.PUT)
    public void reserveOwnersParking(@Valid @RequestBody ParkingNumberRequest request, HttpServletRequest httpRequest) throws ApplicationException {
        parkingService.reserve(request.getNumber(), httpRequest);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.DELETE)
    public void cancelReservation(HttpServletRequest request) throws ApplicationException {
        parkingService.cancelReservation(request);
    }
}