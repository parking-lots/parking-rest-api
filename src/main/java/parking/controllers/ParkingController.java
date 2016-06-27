package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.beans.request.RecallParking;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.exceptions.ApplicationException;
import parking.helper.*;
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
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, httpRequest);
        }

        if (ToolHelper.hasDuplicates(request.getAvailableDates())){
            throw exceptionHandler.handleException(ExceptionMessage.DUBLICATE_DATES, httpRequest);
        }

        AvailableDatesConverter converter = new AvailableDatesConverter();
        List<AvailablePeriod> availablePeriods;

        if (request.getAvailableDates().size() > 0) {

            availablePeriods = converter.convertToInterval(request.getAvailableDates());

            for (AvailablePeriod p : availablePeriods) {
                parkingService.validatePeriod(parking.getNumber(), p.getFreeFrom(), p.getFreeTill(), httpRequest);
            }

            for (AvailablePeriod p : availablePeriods) {
                parkingService.freeOwnersParking(parking.getOwner().getId(), parking.getNumber(), p.getFreeFrom(), p.getFreeTill(), httpRequest);
            }
        }
    }

    @RequestMapping(value = "/availability", method = RequestMethod.DELETE)
    public void recallParking(@Valid @RequestBody RecallParking recallParking, HttpServletRequest request) throws ApplicationException {
        parkingService.recallParking(recallParking.getAvailableDates(), request);
    }

    @RequestMapping(value = "/{lotNumber}/reservation", method = RequestMethod.PUT)
    public void reserveOwnersParking(@PathVariable(value = "lotNumber") Integer lotNumber, HttpServletRequest httpRequest) throws ApplicationException {
        parkingService.reserve(lotNumber, httpRequest);
    }

    @RequestMapping(value = "/reservation", method = RequestMethod.DELETE)
    public void cancelReservation(HttpServletRequest request) throws ApplicationException {
        parkingService.cancelReservation(request);
    }
}
