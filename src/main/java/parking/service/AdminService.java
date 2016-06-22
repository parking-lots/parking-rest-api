package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.ActionType;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private ExceptionHandler exceptionHandler;

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
            Map<String, String> map = new HashMap<>();
            metaData.setFullName(map);
            map.put("old", oldAccount.getFullName());
            map.put("new", newAccount.getFullName());
        }
        if (newAccount.getPassword() != null && !oldAccount.getPassword().equals(newAccount.getPassword())) {
            metaData.setPasswordChanged(true);
        }

        if (!oldAccount.getCarRegNoList().containsAll(newAccount.getCarRegNoList()) || !(newAccount.getCarRegNoList().containsAll(oldAccount.getCarRegNoList()))) {

            Map<String, String[]> carMap = new HashMap<>();
            String[] oldCarArr = new String[oldAccount.getCarRegNoList().size()];
            String[] newCarArr = new String[newAccount.getCarRegNoList().size()];

            for (int i = 0; i < oldAccount.getCarRegNoList().size(); i++) {
                oldCarArr[i] = oldAccount.getCarRegNoList().get(i);

                if (i == oldAccount.getCarRegNoList().size() - 1) {
                    carMap.put("old", oldCarArr);
                }
            }

            for (int i = 0; i < newAccount.getCarRegNoList().size(); i++) {
                newCarArr[i] = newAccount.getCarRegNoList().get(i);

                if (i == newAccount.getCarRegNoList().size() - 1) {
                    carMap.put("new", newCarArr);
                }
            }

            metaData.setCars(carMap);
        }
        if (!oldAccount.getEmail().equals(newAccount.getEmail())) {
            Map<String, String> map = new HashMap<>();
            metaData.setEmail(map);
            map.put("old", oldAccount.getEmail());
            map.put("new", newAccount.getEmail());
        }

        String userAgent = request.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.EDIT_USER, oldAccount.getId(), oldAccount.getParking().getNumber(), null, null, metaData, user.getId(), userAgent);

    }

    public void deleteUser(String username, HttpServletRequest request) throws ApplicationException {
        Account accountToDelete = accountRepository.findByUsername(username);

        if (accountToDelete == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        } else {
            Account user = userService.getCurrentUser(request);
            Integer lotNum = accountToDelete.getParking() == null ? null : accountToDelete.getParking().getNumber();
            String userAgent = request.getHeader("User-Agent");
            logRepository.insertActionLog(ActionType.DELETE_USER, accountToDelete.getId(), lotNum, null, null, null, user.getId(), userAgent);

            accountRepository.deleteByUsername(username);
        }
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
