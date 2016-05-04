package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
@RequestMapping(value = "/v2/parking")
public class ParkingControllerV2 {
    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<Parking> getAllAvailable(HttpServletRequest request) throws ApplicationException {
        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        return parkingService.getAvailable(request).stream()
                .map(mapper)
                .collect(Collectors.<Parking>toList());
    }

    @RequestMapping(value = "/availability", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody SetUnusedRequest request, HttpServletRequest httpRequest) throws ApplicationException {
        parkingService.freeOwnersParking(request, httpRequest);
    }

    @RequestMapping(value = "/availability", method = RequestMethod.DELETE)
    public void recallParking(@Valid @RequestBody RecallSingleParking recallSingleParking) {
        parkingService.recallParking(recallSingleParking);
    }

    @RequestMapping(value = "/{lotNumber}/reservation", method = RequestMethod.PUT)
    public void reserveOwnersParking(@PathVariable(value = "lotNumber") Integer lotNumber, HttpServletRequest httpRequest) throws ApplicationException {
        parkingService.reserve(lotNumber, httpRequest);
    }

    @RequestMapping(value = "/{lotNumber}/reservation", method = RequestMethod.DELETE)
    public void cancelReservation(@PathVariable(value = "lotNumber") Integer lotNumber, HttpServletRequest request) throws ApplicationException {
        parkingService.cancelReservation(request);
    }
}
