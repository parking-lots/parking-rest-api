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
import parking.utils.AccountStatus;
import parking.utils.ActionType;
import parking.utils.ParkingType;

import javax.mail.MessagingException;
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

    public void activateAccount(String username, HttpServletRequest request) {

    }

    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) throws ApplicationException, MessagingException {

        if (newAccount.getCarRegNoList() != null && newAccount.getCarRegNoList().contains("")) {
            throw exceptionHandler.handleException(ExceptionMessage.EMPTY_CAR_REG_NO, request);
        }

        Account oldAccount = accountRepository.findByUsername(username);

        if (oldAccount == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        }

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

        if (oldAccount.getCarRegNoList() != null) {
            Collections.sort(oldAccount.getCarRegNoList());
        }
        if (newAccount.getCarRegNoList() != null) {
            Collections.sort(newAccount.getCarRegNoList());
        }

        if (oldAccount.getStatus() != null && oldAccount.getStatus().equals(AccountStatus.INACTIVE)) {
            if (newAccount.getAccountStatus() != null && newAccount.getAccountStatus().equals(AccountStatus.ACTIVE)) {
                try {
                    MailService.sendEmail(newAccount.getEmail(), "Account activation", "Your account has been activated");
                } catch (Exception e) {
                    throw exceptionHandler.handleException(ExceptionMessage.COULD_NOT_SEND_EMAIL, request);
                }
            }
        }

        checkCars:
        if (oldAccount.getCarRegNoList() == null && newAccount.getCarRegNoList() == null) {
            break checkCars;
        } else if ((oldAccount.getCarRegNoList() == null ^ newAccount.getCarRegNoList() == null) || !(oldAccount.getCarRegNoList().equals(newAccount.getCarRegNoList()))) {
            Map<String, String[]> carMap = new HashMap<>();
            String[] oldCarArr;
            String[] newCarArr;

            if (oldAccount.getCarRegNoList().size() == 0) {
                carMap.put("old", null);
            } else {
                oldCarArr = new String[oldAccount.getCarRegNoList().size()];
                for (int i = 0; i < oldAccount.getCarRegNoList().size(); i++) {
                    oldCarArr[i] = oldAccount.getCarRegNoList().get(i);

                    if (i == oldAccount.getCarRegNoList().size() - 1) {
                        carMap.put("old", oldCarArr);
                    }
                }
            }

            if (newAccount.getCarRegNoList() == null || newAccount.getCarRegNoList().size() == 0) {
                carMap.put("new", null);
            } else {
                newCarArr = new String[newAccount.getCarRegNoList().size()];
                for (int i = 0; i < newAccount.getCarRegNoList().size(); i++) {
                    newCarArr[i] = newAccount.getCarRegNoList().get(i);

                    if (i == newAccount.getCarRegNoList().size() - 1) {
                        carMap.put("new", newCarArr);
                    }
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
        String userAgent = httpRequest.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.ATTACH_PARKING, accountRepository.findByUsername(username), lotNumber, null, null, null, userService.getCurrentUser(httpRequest), userAgent);
    }

    public void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException {
        ParkingLot parkingLot = accountRepository.findByUsername(username).getParking();

        if (parkingLot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }

        accountRepository.detachParking(username, httpRequest);
        lotsRepository.removeParkingOwner(parkingLot.getNumber());
        String userAgent = httpRequest.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.DETACH_PARKING, accountRepository.findByUsername(username), parkingLot.getNumber(), null, null, null, userService.getCurrentUser(httpRequest), userAgent);
    }

    public List<FreeParkingLot> getParkings(ParkingType type) {
        return lotsRepository.findParking(type).stream()
                .map(FreeParkingLot::new)
                .collect(Collectors.toList());
    }
}
