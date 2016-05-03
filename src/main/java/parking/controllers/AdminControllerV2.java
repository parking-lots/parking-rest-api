package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.document.ParkingLot;
import parking.beans.request.DeleteUser;
import parking.beans.request.EditUserForm;
import parking.beans.request.RegistrationForm;
import parking.beans.request.alterParking;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.exceptions.UserException;
import parking.service.AdminService;
import parking.service.AdminServiceV2;
import parking.service.RegistrationService;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/v2/admin")
public class AdminControllerV2 {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminServiceV2 adminService;

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public Profile createUser(@Valid @RequestBody RegistrationForm form, HttpServletRequest request) throws UserException, ApplicationException {
        return new Profile(registrationService.registerUser(form.getAccount(), form.getParking(), request));
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> displayUsers(HttpServletRequest request) throws ApplicationException {
        return adminService.getUsers();
    }

    @RequestMapping(value = "/users/{username}", method = RequestMethod.POST)
    public void editUser(@Valid @RequestBody EditUserForm form, @PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException {
        adminService.editUser(form.getAccount(), request);
    }

    @RequestMapping(value = "/users/{username}", method = RequestMethod.DELETE)
    public Long deleteUser(@PathVariable(value = "username") String username) {
        return adminService.deleteUser(username);
    }

    @RequestMapping(value = "/parkings/{type}", method = RequestMethod.GET)
    public List<ParkingLot> getParkings(@PathVariable(value = "type") ParkingType type){
        return adminService.getParkings(type);
    }
}
