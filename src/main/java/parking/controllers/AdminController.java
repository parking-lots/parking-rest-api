package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import parking.beans.request.AttachParking;
import parking.beans.request.EditUserForm;
import parking.beans.request.LoginForm;
import parking.beans.request.RegistrationForm;
import parking.beans.response.FreeParkingLot;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.service.AdminService;
import parking.service.ParkingService;
import parking.service.RegistrationService;
import parking.utils.ParkingType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private ParkingService parkingService;

    @RequestMapping(value = "/users", method = RequestMethod.PUT)
    public Profile createUser(@Valid @RequestBody RegistrationForm form, HttpServletRequest request) throws ApplicationException, MessagingException {
        boolean parkingLot = form.getNumber() == null ? false : true;

        return new Profile(registrationService.registerUser(form.getAccount(), form.getNumber(), request), parkingLot);
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> displayUsers(HttpServletRequest request) throws ApplicationException {
        return adminService.getUsers();
    }

    @RequestMapping(value = "/users/{username:.+}/activate", method = RequestMethod.POST)
    public void confirmAccount(@PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException {
        adminService.activateAccount(username, request);
    }

    @RequestMapping(value = "/users/{username:.+}", method = RequestMethod.POST)
    public void editUser(@Valid @RequestBody EditUserForm form, @PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException, MessagingException {
        adminService.editUser(form, username, request);
    }

    @RequestMapping(value = "/users/{username:.+}", method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable(value = "username") String username, HttpServletRequest request) throws ApplicationException {
        adminService.deleteUser(username, request);
    }

    @RequestMapping(value = "/parkings/{type}", method = RequestMethod.GET)
    public List<FreeParkingLot> getParkings(@PathVariable(value = "type") ParkingType type){
        return adminService.getParkings(type);
    }

    @RequestMapping(value = "/users/{username:.+}/parking/attach", method = RequestMethod.POST)
    public void attachParking(@Valid @RequestBody AttachParking attachParking, @PathVariable(value = "username") String username, HttpServletRequest httpRequest) throws ApplicationException{
        adminService.attachParking(attachParking.getLotNumber(), username, httpRequest);
    }

    @RequestMapping(value = "/users/{username:.+}/parking/detach", method = RequestMethod.POST)
    public void detachParking(@PathVariable(value = "username") String username, HttpServletRequest httpRequest) throws ApplicationException{
        adminService.detachParking(username, httpRequest);
    }
}
