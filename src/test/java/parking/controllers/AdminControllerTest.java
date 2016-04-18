package parking.controllers;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.request.RegistrationForm;
import parking.beans.response.User;
import parking.builders.LotsBuilder;
import parking.builders.UserBuilder;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.service.AdminService;
import parking.service.RegistrationService;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private AdminService adminService;

    @Mock
    HttpServletRequest httpRequest;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private AccountRepository accountRepository;

    @Test
    public void createUserMustBeMethod() throws NoSuchMethodException {
        assertEquals(AdminController.class.getMethod("createUser", RegistrationForm.class,
                HttpServletRequest.class).getName(), "createUser");
    }

    private List<User> mockedUserList = new ArrayList<User>();

    @Before
    public void initMockData(){

//        mockedUserList.add(new UserBuilder("Name1","nick1","owner","111"));
//        mockedUserList.add(new UserBuilder().number(101).build());
//        mockedUserList.add(new UserBuilder().number(103).build());
//        mockedUserList.add(new UserBuilder().number(104).build());
    }

    @Test
    public void whenRegisterUserShouldCallService() throws UserException, ParkingException {
        RegistrationForm form = new RegistrationForm();
        form.setAccount(new Account("fullName", "username", "passwrod"));
        form.setParking(null);
    }

    @Test
    public void whenDisplayUsersShouldCallService() throws UserException, ParkingException {
        given(adminService.getUsers()).willReturn(mockedUserList);
    }
}
