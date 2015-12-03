package loans.controllers;

import loans.beans.request.Customer;
import loans.beans.response.Loan;
import loans.beans.response.ParkingLot;
import loans.exceptions.ExceedLoanLimitException;
import loans.exceptions.MonthlyPaymentException;
import loans.service.LoanService;
import loans.service.ParkingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/parking")
public class LoanController extends BaseController {

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/available", method = RequestMethod.GET)
    public List<ParkingLot> getAllAvailable() {

        return parkingService.getAvailable();
    }

    @RequestMapping(value = "/available", method = RequestMethod.DELETE)
    public List<ParkingLot> recallParking() {
        return null;
    }

    @RequestMapping(value = "/available", method = RequestMethod.PUT)
    public List<ParkingLot> freeParking() {
        return null;
    }


}
