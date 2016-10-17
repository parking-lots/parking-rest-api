package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.RecallParking;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.exceptions.ApplicationException;
import parking.helper.*;
import parking.repositories.LotsRepository;
import parking.service.ParkingService;
import parking.service.UserService;

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
    @Autowired
    private UserService userService;
    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private parking.helper.ExceptionHandler exceptionHandler;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<Parking> getAllAvailable(HttpServletRequest request) throws ApplicationException {
        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        return parkingService.getAvailable(request).stream()
                .map(mapper)
                .collect(Collectors.<Parking>toList());
    }

    @RequestMapping(value = "/availability", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody SetUnusedRequest request, HttpServletRequest httpRequest) throws ApplicationException {
        ParkingLot parking = parkingService.getParkingNumberByUser();

        if (parking == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }
        parkingService.shareParking(parking.getOwner(), request.getAvailableDates());
    }

    @RequestMapping(value = "/availability", method = RequestMethod.DELETE)
    public void recallParking(@Valid @RequestBody RecallParking recallParking, HttpServletRequest request) throws ApplicationException {
        ParkingLot parking = parkingService.getParkingNumberByUser();
        parkingService.unshareParking(parking.getOwner(), recallParking.getAvailableDates());
    }

    @RequestMapping(value = "/{lotNumber}/reservation", method = RequestMethod.PUT)
    public void reserveOwnersParking(@PathVariable(value = "lotNumber") Integer lotNumber, HttpServletRequest httpRequest) throws ApplicationException {
        Account account = userService.getCurrentUser();
        parkingService.reserve(lotNumber, account, httpRequest);
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.DELETE)
    public void cancelReservation(HttpServletRequest request) throws ApplicationException {
        Account account = userService.getCurrentUser();
        ParkingLot lot = lotsRepository.findByUser(account);
        parkingService.cancelReservation(lot, account, request);
    }
}
