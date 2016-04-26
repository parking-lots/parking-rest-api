package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
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

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;

@RestController
@RequestMapping(value = "/profile")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ExceptionHandler exceptionHandler;

    @RequestMapping(method = RequestMethod.GET)
    public Profile profile(HttpServletRequest request, Principal principal) throws ApplicationException {
        if (principal == null) {
            throw exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, request);
        }
        return userService.getCurrentUserProfile();
    }

    @RequestMapping(value = "/password", method = RequestMethod.PUT)
    public void changePassword(@Valid @RequestBody ChangePassword password, HttpServletRequest request) throws ApplicationException {
        userService.changePassword(password, request);
    }
}
