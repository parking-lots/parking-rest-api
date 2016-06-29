package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.FreeParkingLot;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.repositories.RoleRepository;
import parking.utils.ActionType;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
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

    @Autowired
    public RoleRepository roleRepository;

    public List<User> getUsers() {

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());

    }

    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) throws ApplicationException {

        Account oldAccount = accountRepository.findByUsername(username);

        accountRepository.editAccount(newAccount, oldAccount, username);

        Account user = userService.getCurrentUser(request);
        LogMetaData metaData = new LogMetaData();

        if (!oldAccount.getFullName().equals(newAccount.getFullName())) {
            Map<String, String> map = new HashMap<>();
            metaData.setFullName(map);
            map.put("old", oldAccount.getFullName());
            map.put("new", newAccount.getFullName());
        }
        //if received password is null - means it hasn't been changed
        if (newAccount.getPassword() != null) {
            metaData.setPasswordChanged(true);
        }

        Collections.sort(oldAccount.getCarRegNoList());
        Collections.sort(newAccount.getCarRegNoList());

        checkCars:
        if (oldAccount.getCarRegNoList() == null && newAccount.getCarRegNoList() == null) {
            break checkCars;
        } else if ((oldAccount.getCarRegNoList() == null ^ newAccount.getCarRegNoList() == null) || !(oldAccount.getCarRegNoList().equals(newAccount.getCarRegNoList())))
        {
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

        if (newAccount.getEmail() == null) {
            Map<String, String> map = new HashMap<>();
            metaData.setEmail(map);
            map.put("old", oldAccount.getEmail());
            map.put("new", null);
        } else if (!newAccount.getEmail().equals(oldAccount.getEmail())) {
            Map<String, String> map = new HashMap<>();
            metaData.setEmail(map);
            map.put("old", oldAccount.getEmail());
            map.put("new", newAccount.getEmail());
        }

        String userAgent = request.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.EDIT_USER, oldAccount, null, null, null, metaData, user, userAgent);

    }

    public void deleteUser(String username, HttpServletRequest request) throws ApplicationException {
        Account accountToDelete = accountRepository.findByUsername(username);

        if (accountToDelete == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        } else {
            Account user = userService.getCurrentUser(request);
            Integer lotNum = accountToDelete.getParking() == null ? null : accountToDelete.getParking().getNumber();
            String userAgent = request.getHeader("User-Agent");
            logRepository.insertActionLog(ActionType.DELETE_USER, accountToDelete, lotNum, null, null, null, user, userAgent);

            if (lotNum != null) {
                lotsRepository.removeParkingOwner(accountToDelete.getParking().getNumber());
            }

            accountRepository.deleteByUsername(username);
        }
    }

    public void attachParking(Integer lotNumber, String username, HttpServletRequest httpRequest) throws ApplicationException {
        accountRepository.attachParking(lotNumber, username, httpRequest);
    }

    public void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException {
        ParkingLot parkingLot = accountRepository.findByUsername(username).getParking();

        if (parkingLot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }

        accountRepository.detachParking(username, httpRequest);
        lotsRepository.removeParkingOwner(parkingLot.getNumber());

    }

    public List<FreeParkingLot> getParkings(ParkingType type) {
        return lotsRepository.findParking(type).stream()
                .map(FreeParkingLot::new)
                .collect(Collectors.toList());
    }
}
