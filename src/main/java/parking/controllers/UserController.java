package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.request.ChangePassword;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @Autowired
    private HttpServletResponse response;

    @RequestMapping(method = RequestMethod.GET)
    public Profile profile(HttpServletRequest request, Principal principal) throws ApplicationException {

        String username = null, password = null;

            for (Cookie cookie : request.getCookies()) {

                if (cookie.getName().equals("username")) {
                    username = cookie.getValue();
                }
                if (cookie.getName().equals("password")) {
                    password = cookie.getValue();
                }
            }

        if (principal == null && username == null && password == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }
        else if (principal == null && username != null && password != null){
            userService.rememberMeLogin(username, password, request);
        }

            return userService.getCurrentUserProfile();

    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public void changePassword(@Valid @RequestBody ChangePassword password, HttpServletRequest request) throws ApplicationException {
        userService.changePassword(password, request);
    }
}
