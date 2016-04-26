package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class RegistrationService {


    @Autowired
    private UserService userService;

    @Autowired
    private ParkingService parkingService;

    public Account registerUser(Account user, ParkingLot parking, HttpServletRequest request) throws ApplicationException {
        Account createdAccount = userService.createUser(user, request);

        if (Optional.ofNullable(parking).isPresent()) {
            ParkingLot createdParking = parkingService.createLot(parking, request);
            userService.attachParking(createdAccount, createdParking.getNumber(), request);
            parkingService.setOwner(createdAccount, createdParking);
        }

        return createdAccount;
    }
}
