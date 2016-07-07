package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;
import parking.repositories.AccountRepositoryImpl;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@Service
public class RegistrationService {


    @Autowired
    private UserService userService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ParkingService parkingService;

    public Account registerUser(Account user, Integer number, HttpServletRequest request) throws ApplicationException, MessagingException {
        Account createdAccount = userService.createUser(user, request);

        if (Optional.ofNullable(number).isPresent()) {
            accountRepository.attachParking(number, createdAccount.getUsername(), request);
        }

        return accountRepository.findByUsername(createdAccount.getUsername());
    }
}
