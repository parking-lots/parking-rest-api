package parking.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.Account;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value = "/user")
public class AuthenticationController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public void Login(@Valid @RequestBody LoginForm user, HttpServletRequest request) throws UserException {
        userService.login(user, request);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.DELETE)
    public void logout(HttpSession session, Principal principal) throws UserException {
        if (principal == null) {
            throw new UserException("not_logged");
        }
        session.invalidate();
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public Profile profile(HttpServletRequest session, Principal principal) throws UserException {
        if (principal == null) {
            throw new UserException("not_logged");
        }
        return userService.getCurrentUserProfile();
    }
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void createUser() {
        userService.createUser();
    }
}

