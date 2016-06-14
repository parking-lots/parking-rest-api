package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.Car;
import parking.beans.document.LogMetaData;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.ActionType;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LotsRepository lotsRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private UserService userService;

    public List<User> getUsers() {

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());

    }

    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) throws ApplicationException {

        Account oldAccount = accountRepository.findByUsername(username);

        accountRepository.editAccount(newAccount, username);

        Account user = userService.getCurrentUser(request);
        LogMetaData metaData = new LogMetaData();

        if (!oldAccount.getFullName().equals(newAccount.getFullName())) {
            ArrayList<String> arrayList = new ArrayList<>();
            metaData.setFullName(arrayList);
            arrayList.add(oldAccount.getFullName());
            arrayList.add(newAccount.getFullName());
        }
        if (!oldAccount.getPassword().equals(newAccount.getPassword())) {
            metaData.setPasswordChanged(true);
        }

        if (!oldAccount.getCarRegNOList().containsAll(newAccount.getCarRegNoList()) || !(newAccount.getCarRegNoList().containsAll(oldAccount.getCarRegNOList()))) {

            String[][] carNumbers = new String[2][2];

            for (int i = 0; i < oldAccount.getCarRegNOList().size(); i++) {
                carNumbers[0][i] = oldAccount.getCarRegNOList().get(i);
            }

            for (int i = 0; i < newAccount.getCarRegNoList().size(); i++) {
                carNumbers[1][i] = newAccount.getCarRegNoList().get(i);
            }

            metaData.setCars(carNumbers);
        }
        if(!oldAccount.getEmail().equals(newAccount.getEmail())){
            ArrayList<String> arrayList = new ArrayList<>();
            metaData.setEmail(arrayList);
            arrayList.add(oldAccount.getEmail());
            arrayList.add(newAccount.getEmail());
        }

        logRepository.insertActionLog(ActionType.EDIT_USER, oldAccount.getId(), oldAccount.getParking().getNumber(), null, null, metaData, user.getId(), null);

    }

    public Long deleteUser(String username, HttpServletRequest request) throws ApplicationException {

        Long numberOfDeletedAccounts = accountRepository.deleteByUsername(username);

        Account deletedAccount = accountRepository.findByUsername(username);
        Account user = userService.getCurrentUser(request);
        logRepository.insertActionLog(ActionType.DELETE_USER, deletedAccount.getId(), deletedAccount.getParking().getNumber(), null, null, null, user.getId(), null);

        return numberOfDeletedAccounts;
    }

    public void attachParking(Integer lotNumber, String username) {
        accountRepository.attachParking(lotNumber, username);
    }

    public void removeParkingFromUser(String username) {
        accountRepository.removeParking(username);
    }

    public List<ParkingLot> getParkings(ParkingType type) {

        if (type == null) return lotsRepository.findAll();
        return lotsRepository.findParking(type);
    }
}
