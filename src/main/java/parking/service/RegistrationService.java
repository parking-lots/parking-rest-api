package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.UserException;

@Service
public class RegistrationService {


    @Autowired
    private UserService userService;

    public Account registerUser(Account user, ParkingLot parking) throws UserException {
        Account createdAccount = userService.createUser(user);

        return createdAccount;
    }
}
