package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.FreeParkingLot;
import parking.beans.response.LogResponse;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.helper.ProfileHelper;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.repositories.RoleRepository;
import parking.utils.ActionType;
import parking.utils.EmailDomain;
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

    public List<LogResponse> getLog() {
        return logRepository.findAll().stream()
                .map(LogResponse::new)
                .collect(Collectors.toList());
    }

    public void editUser(EditUserForm newAccount, String username, HttpServletRequest request) throws ApplicationException, MessagingException {

        if (newAccount.getCarRegNoList() != null && newAccount.getCarRegNoList().contains("")) {
            throw exceptionHandler.handleException(ExceptionMessage.EMPTY_CAR_REG_NO, request);
        }

        Account oldAccount = accountRepository.findByUsername(username);

        if (oldAccount == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        }

        String email = newAccount.getEmail();
        if (email != null && !email.substring(email.indexOf("@") + 1).equals(EmailDomain.SWEDBANK_LT.getDomain())) {
            throw exceptionHandler.handleException(ExceptionMessage.INVALID_EMAIL, request);
        }

        accountRepository.editAccount(newAccount, oldAccount, username, request);

        Account user = userService.getCurrentUser(request);
        LogMetaData metaData = getLogMetaData(newAccount, oldAccount);
        String userAgent = request.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.EDIT_USER, oldAccount, null, null, null, metaData, user, userAgent);

    }

    private LogMetaData getLogMetaData(EditUserForm newAccount, Account oldAccount) {
        LogMetaData metaData = new LogMetaData();

        if (newAccount.getFullName() != null && !oldAccount.getFullName().equals(newAccount.getFullName())) {
            Map<String, String> map = new HashMap<>();
            metaData.setFullName(map);
            map.put("old", oldAccount.getFullName());
            map.put("new", newAccount.getFullName());
        }
        //if received password is null - means it hasn't been changed
        if (newAccount.getPassword() != null && !ProfileHelper.checkPassword(newAccount.getPassword(), oldAccount.getPassword())) {
            metaData.setPasswordStatus("changed");
        }

        Collections.sort(oldAccount.getCarRegNoList());
        Collections.sort(newAccount.getCarRegNoList());

        Map<String, String[]> carMap = new HashMap<>();
        String[] oldCarArr;
        String[] newCarArr;

        if (!oldAccount.getCarRegNoList().equals(newAccount.getCarRegNoList())) {
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

            if (newAccount.getCarRegNoList().size() == 0) {
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


        if (newAccount.getEmail() != null && !newAccount.getEmail().equals(oldAccount.getEmail())) {
            Map<String, String> map = new HashMap<>();
            metaData.setEmail(map);
            map.put("old", oldAccount.getEmail());
            map.put("new", newAccount.getEmail());
        }

        if (newAccount.isActive() && oldAccount.isActive() != newAccount.isActive()) {
            Map<String, Boolean> map = new HashMap<>();
            metaData.setActive(map);
            map.put("old", oldAccount.isActive());
            map.put("new", newAccount.isActive());
        }
        return metaData;
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
        Account user = userService.getCurrentUser(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.ATTACH_PARKING, accountRepository.findByUsername(username), lotNumber, null, null, null, user, userAgent);
    }

    public void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException {
        ParkingLot parkingLot = accountRepository.findByUsername(username).getParking();

        if (parkingLot == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }

        accountRepository.detachParking(username, httpRequest);
        lotsRepository.removeParkingOwner(parkingLot.getNumber());
        Account user = userService.getCurrentUser(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.DETACH_PARKING, accountRepository.findByUsername(username), parkingLot.getNumber(), null, null, null, user, userAgent);
    }

    public List<FreeParkingLot> getParkings(ParkingType type) {
        return lotsRepository.findParking(type).stream()
                .map(FreeParkingLot::new)
                .collect(Collectors.toList());
    }
}
