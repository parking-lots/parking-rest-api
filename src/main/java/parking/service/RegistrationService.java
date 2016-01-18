package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;

import java.util.Optional;

@Service
public class RegistrationService {


    @Autowired
    private UserService userService;

    @Autowired
    private ParkingService parkingService;

    public Account registerUser(Account user, ParkingLot parking) throws UserException, ParkingException {
        Account createdAccount = userService.createUser(user);

        if(Optional.ofNullable(parking).isPresent()) {
            ParkingLot createdParking = parkingService.createLot(parking);
            userService.attachParking(createdAccount, createdParking.getNumber());
            parkingService.setOwner(createdAccount, createdParking);
        }

        return createdAccount;
    }
}
