package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.request.ChangePassword;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

import static com.sun.javaws.JnlpxArgs.verify;


@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

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

        session.invalidate();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public Profile profile(HttpServletRequest request) throws ApplicationException {
        return userService.getCurrentUserProfile(request);
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public void changePassword(@Valid @RequestBody ChangePassword password, HttpServletRequest request) throws ApplicationException {
        userService.changePassword(password, request);
    }
}
