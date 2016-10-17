package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.Account;
import parking.beans.request.*;
import parking.beans.response.ConfirmationResponse;
import parking.beans.response.Profile;
import parking.beans.response.Response;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.service.AdminService;
import parking.service.RegistrationService;
import parking.service.UserService;
import parking.utils.ActionType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private LogRepository logRepository;

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminService adminService;

    @RequestMapping(method = RequestMethod.PUT)
    public Profile createUser(@Valid @RequestBody RegistrationForm form, HttpServletRequest request) throws ApplicationException, MessagingException {
        boolean parkingLot = form.getNumber() == null ? false : true;

        return new Profile(registrationService.registerUser(form.getAccount(), form.getNumber(), request), parkingLot);
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void login(@Valid @RequestBody LoginForm user, HttpServletRequest request) throws ApplicationException {
        userService.login(user.getUsername(), user.getPassword(), user.getRemember(), request);
    }

    @RequestMapping(value = "/login", method = RequestMethod.DELETE)
    public void logout(@CookieValue(value = "username", defaultValue = "") String username, @CookieValue(value = "password", defaultValue = "") String password, HttpSession session, Principal principal, HttpServletRequest request) throws ApplicationException {
        if (principal == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }

        userService.deleteCookies(username, password);

        Account user = userService.getCurrentUser();
        session.invalidate();

        String userAgent = request.getHeader("User-Agent");
        logRepository.insertActionLog(ActionType.LOG_OUT, null, null, null, null, null, user, userAgent);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public Profile profile(HttpServletRequest request) throws ApplicationException {
        return userService.getCurrentUserProfile(request);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public void editUser(@Valid @RequestBody EditUserForm form, HttpServletRequest request) throws ApplicationException, MessagingException {
        String username = userService.getCurrentUser().getUsername();
        adminService.editUser(form, username, request);

    }

    @RequestMapping(value = "/{confirmationKey}", method = RequestMethod.POST)
    public ConfirmationResponse confirmEmail(@PathVariable(value = "confirmationKey") String confirmationKey, HttpServletRequest request) throws ApplicationException {
        if (userService.confirmEmail(confirmationKey, request)) {
            return new ConfirmationResponse("Your e-mail has been successfully verified. We will inform you when administrator will register your car numbers and activate your account.");
        } else {
            throw exceptionHandler.handleException(ExceptionMessage.CONFIRMATION_FAILED, request);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    public void resetPassword(@Valid @RequestBody ResetPassword resetPassword, HttpServletRequest httpRequest) throws MessagingException, ApplicationException {
        userService.resetPassword(resetPassword.getEmail(), httpRequest);
    }

}
