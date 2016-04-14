package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.document.ParkingLot;
import parking.beans.request.RegistrationForm;
import parking.beans.response.Parking;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;
import parking.service.AdminService;
import parking.service.RegistrationService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminService adminService;

    @RequestMapping(value = "/user/create", method = RequestMethod.POST)
    public Profile createUser(@Valid @RequestBody RegistrationForm form, HttpServletRequest request) throws UserException, ApplicationException {
        return new Profile(registrationService.registerUser(form.getAccount(), form.getParking(), request));
    }

    @RequestMapping(value = "/userlist", method = RequestMethod.GET)
    public List<User> displayUsers(HttpServletRequest request) throws UserException, ApplicationException {
        return adminService.getUsers();

    }
}
