package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.request.ChangePassword;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(method = RequestMethod.GET)
    public Profile profile(HttpServletRequest session, Principal principal) throws UserException {
        if (principal == null) {
            throw new UserException("not_logged");
        }
        return userService.getCurrentUserProfile();
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public void changePassword(@Valid @RequestBody ChangePassword password) throws UserException {
        userService.changePassword(password);
    }
}
