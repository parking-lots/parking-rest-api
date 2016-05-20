package parking.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
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
@Deprecated
@RequestMapping(value = "/user")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void Login(@Valid @RequestBody LoginForm user, HttpServletRequest request) throws ApplicationException {

        userService.login(user.getUsername(), user.getPassword(), user.getRemember(), request);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public void logout(HttpSession session, Principal principal, HttpServletRequest request) throws ApplicationException {
        if (principal == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }

        userService.deleteCookies(request);

        session.invalidate();
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void createUser() {
//        userService.createUser();
    }

}

