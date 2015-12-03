package loans.controllers;

import loans.beans.request.parkingNumberRequest;
import loans.beans.request.setUnusedRequest;
import loans.beans.response.ParkingLot;
import loans.service.ParkingService;
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
    public void recallParking(@Valid @RequestBody parkingNumberRequest request) {
        parkingService.recallParking(request);
    }

    @RequestMapping(value = "/available", method = RequestMethod.PUT)
    public void freeOwnersParking(@Valid @RequestBody setUnusedRequest request) {
        parkingService.freeOwnersParking(request);
    }
}