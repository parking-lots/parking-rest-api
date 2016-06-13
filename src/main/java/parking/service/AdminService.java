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

    public void editUser(Account account, HttpServletRequest request) throws ApplicationException {

        Account oldAccount = accountRepository.findByUsername(account.getUsername());
    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) {

        accountRepository.editAccount(account);

        Account user = userService.getCurrentUser(request);
        LogMetaData metaData = new LogMetaData();

        if (!oldAccount.getFullName().equals(account.getFullName())) {
            ArrayList<String> arrayList = new ArrayList<>();
            metaData.setFullName(arrayList);
            arrayList.add(oldAccount.getFullName());
            arrayList.add(account.getFullName());
        }
        if (!oldAccount.getPassword().equals(account.getPassword())) {
            metaData.setPasswordChanged(true);
        }
        if (!oldAccount.getParking().getNumber().equals(account.getParking().getNumber())) {
            metaData.setPasswordChanged(true);
        }
        if (!(oldAccount.getCarList().containsAll(account.getCarList())) || !(account.getCarList().containsAll(oldAccount.getCarList()))) {

            String[][] carNumbers = new String[2][2];

            for (int i = 0; i < oldAccount.getCarList().size(); i++) {
                carNumbers[0][i] = oldAccount.getCarList().get(i).getRegNo();
            }

            for (int i = 0; i < account.getCarList().size(); i++) {
                carNumbers[1][i] = account.getCarList().get(i).getRegNo();
            }

            metaData.setCars(carNumbers);
        }
        if(!oldAccount.getEmail().equals(account.getEmail())){
            ArrayList<String> arrayList = new ArrayList<>();
            metaData.setEmail(arrayList);
            arrayList.add(oldAccount.getEmail());
            arrayList.add(account.getEmail());
        }

        logRepository.insertActionLog(ActionType.EDIT_USER, account.getId(), account.getParking().getNumber(), null, null, metaData, user.getId(), null);

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
