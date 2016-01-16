package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.request.RegistrationForm;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.service.RegistrationService;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private RegistrationService registrationService;

    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Profile createUser(@Valid @RequestBody RegistrationForm form) throws UserException {
        return  new Profile(registrationService.registerUser(form.getAccount(), form.getParking()));
    }
}
