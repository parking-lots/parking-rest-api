package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.Account;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.beans.request.*;
import parking.beans.response.FreeParkingLot;
import parking.beans.response.LogResponse;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.helper.*;
import parking.helper.ExceptionHandler;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.service.AdminService;
import parking.service.ParkingService;
import parking.service.RegistrationService;
import parking.utils.ParkingType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminService adminService;
    @Autowired
    private ParkingService parkingService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private LotsRepository lotsRepository;
    @Autowired
    private ExceptionHandler exceptionHandler;

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public Profile createUser(@Valid @RequestBody RegistrationForm form, HttpServletRequest request) throws ApplicationException, MessagingException {
        boolean parkingLot = form.getNumber() == null ? false : true;

        return new Profile(registrationService.registerUser(form.getAccount(), form.getNumber(), request), parkingLot);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> displayUsers(HttpServletRequest request) throws ApplicationException {
        return adminService.getUsers();
    }

    @RequestMapping(value = "/users/{username:.+}", method = RequestMethod.POST)
    public void editUser(@Valid @RequestBody EditUserForm form, @PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException, MessagingException {
        adminService.editUser(form, username, request);
    }

    @RequestMapping(value = "/users/{username:.+}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException {
        adminService.deleteUser(username, request);
    }

    @RequestMapping(value = "/parkings/{type}", method = RequestMethod.GET)
    public List<FreeParkingLot> getParkings(@PathVariable(value = "type") ParkingType type) {
        return adminService.getParkings(type);
    }

    @RequestMapping(value = "/users/{username:.+}/parking/attach", method = RequestMethod.POST)
    public void attachParking(@Valid @RequestBody AttachParking attachParking, @PathVariable(value = "username") String username, HttpServletRequest httpRequest) throws ApplicationException {
        adminService.attachParking(attachParking.getLotNumber(), username, httpRequest);
    }

    @RequestMapping(value = "/users/{username:.+}/parking/detach", method = RequestMethod.POST)
    public void detachParking(@PathVariable(value = "username") String username, HttpServletRequest httpRequest) throws ApplicationException {
        adminService.detachParking(username, httpRequest);
    }

    @RequestMapping(value = "/log", method = RequestMethod.GET)
    public List<LogResponse> displayLog() throws ApplicationException {
        return adminService.getLog();
    }

    @RequestMapping(value = "/users/{username:.+}/parking/availability", method = RequestMethod.PUT)
    public void freeUsersParking(@Valid @RequestBody SetUnusedRequest request, @PathVariable(value = "username") String username, HttpServletRequest httpRequest) throws ApplicationException {
        Account owner = accountRepository.findByUsername(username);
        if (owner == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, httpRequest);
        }

        if (owner.getParking() == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, httpRequest);
        }

        if (ToolHelper.hasDuplicates(request.getAvailableDates())) {
            throw exceptionHandler.handleException(ExceptionMessage.DUBLICATE_DATES, httpRequest);
        }

        AvailableDatesConverter converter = new AvailableDatesConverter();
        List<AvailablePeriod> availablePeriods;

        if (request.getAvailableDates().size() > 0) {

            availablePeriods = converter.convertToInterval(request.getAvailableDates());

            for (AvailablePeriod p : availablePeriods) {
                parkingService.validatePeriod(owner.getParking().getNumber(), p.getFreeFrom(), p.getFreeTill(), httpRequest);
            }

            for (AvailablePeriod p : availablePeriods) {
                parkingService.freeOwnersParking(owner.getParking().getOwner(), owner.getParking().getNumber(), p.getFreeFrom(), p.getFreeTill(), httpRequest);
            }
        }
    }

    @RequestMapping(value = "/users/{username:.+}/parking/availability", method = RequestMethod.DELETE)
    public void recallParking(@Valid @RequestBody RecallParking recallParking, @PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException {
        Account owner = accountRepository.findByUsername(username);
        if (owner == null) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, request);
        }

        if (owner.getParking() == null) {
            throw exceptionHandler.handleException(ExceptionMessage.DOES_NOT_HAVE_PARKING, request);
        }

        parkingService.recallParking(owner.getParking(), recallParking.getAvailableDates(), request);
    }
}
