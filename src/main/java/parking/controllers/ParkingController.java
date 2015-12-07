package parking.controllers;

import parking.beans.request.parkingNumberRequest;
import parking.beans.request.setUnusedRequest;
import parking.beans.response.ParkingLot;
import parking.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/parking")
public class ParkingController extends BaseController {

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<ParkingLot> getAllAvailable() {

        List<ParkingLot> parkingLots = parkingService.getAvailable();

        return parkingLots;
    }

    @RequestMapping(value = "/available", method = RequestMethod.DELETE)
    public void recallParking() {
        parkingService.recallParking();
    }

    @RequestMapping(value = "/available", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody setUnusedRequest request) {
        parkingService.freeOwnersParking(request);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody parkingNumberRequest request) {
        parkingService.reserve(request);
    }

    @RequestMapping(value = "/reserved", method = RequestMethod.DELETE)
    public void cancelRezervation() {
        parkingService.cancelRezervation();
        
    }
}