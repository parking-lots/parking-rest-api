package parking.controllers;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.ParkingLot;
import parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/parking")
public class ParkingController {

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<ParkingLot> getAllAvailable() {

        List<ParkingLot> parkingLots = parkingService.getAvailable();

        return parkingLots;
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
    public void freeOwnersParking(@Valid @RequestBody ParkingNumberRequest request) {
        parkingService.reserve(request);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.DELETE)
    public void cancelRezervation() {
        parkingService.cancelRezervation();
    }
}