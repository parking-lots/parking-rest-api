package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.User;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LotsRepository lotsRepository;

    public List<User> getUsers() {

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());

    }

    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) {

        accountRepository.editAccount(newAccount, username);
    }

    public Long deleteUser(String username) {

        return accountRepository.deleteByUsername(username);
    }

    public void attachParking(Integer lotNumber, String username) {
        accountRepository.attachParking(lotNumber, username);
    }

    public void removeParkingFromUser(String username){
        accountRepository.removeParking(username);
    }

    public List<ParkingLot> getParkings(ParkingType type) {

        if (type == null) return lotsRepository.findAll();
        return lotsRepository.findParking(type);
    }
}
