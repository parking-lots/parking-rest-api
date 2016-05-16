package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.request.ChangePassword;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/v2/user")
public class UserControllerV2 {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void Login(@Valid @RequestBody LoginForm user, HttpServletRequest request) throws ApplicationException {
        userService.login(user.getUsername(), user.getPassword(), user.getRemember(), request);
    }

    @RequestMapping(value = "/login", method = RequestMethod.DELETE)
    public void logout(HttpSession session, Principal principal, HttpServletRequest request) throws ApplicationException {
        if (principal == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }

        userService.deleteCookies(request);

        session.invalidate();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public Profile profile(HttpServletRequest request, Principal principal) throws ApplicationException {

        String username = null, password = null;
        Cookie[] cookies = request.getCookies();

        for (int i = 0; i < cookies.length; i++) {

            if (cookies[i].getName().equals("username")) {
                username = cookies[i].getValue();
            }
            if (cookies[i].getName().equals("password")) {
                password = cookies[i].getValue();
            }
        }

        if (principal == null && username == null && password == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }
        else if (username != null && password != null){
            userService.rememberMeLogin(username, password, request);
        }

        return userService.getCurrentUserProfile();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.POST)
    public void changePassword(@Valid @RequestBody ChangePassword password, HttpServletRequest request) throws ApplicationException {
        userService.changePassword(password, request);
    }
}
