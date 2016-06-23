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

    public Account registerUser(Account user, int number, HttpServletRequest request) throws ApplicationException {
        Account createdAccount = userService.createUser(user, request);

        if (Optional.ofNullable(number).isPresent()) {
            userService.attachParking(createdAccount, number, request);
            ParkingLot attachedParking = parkingService.getParkingByNumber(number, request);
            parkingService.setOwner(createdAccount, attachedParking);
        }

        return createdAccount;
    }
}
