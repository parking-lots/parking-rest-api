package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.response.User;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceV2 {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LotsRepository lotsRepository;

    public List<User> getUsers() {

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());

    }

    public void editUser(Account account, HttpServletRequest request) {

        accountRepository.editAccount(account);
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

    public List<ParkingLot> getParkings(String type) {

        if (type != null) {
            switch (type) {
                case "unassigned": return lotsRepository.findUnassignedParking();
            }
        }

        return lotsRepository.findAll();
    }
}
