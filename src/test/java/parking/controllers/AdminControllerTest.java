package parking.controllers;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.request.EditUserForm;
import parking.beans.request.RegistrationForm;
import parking.beans.response.Parking;
import parking.beans.response.User;
import parking.builders.AccountBuilder;
import parking.builders.LotsBuilder;
import parking.builders.UserBuilder;
import parking.exceptions.ApplicationException;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.service.AdminService;
import parking.service.RegistrationService;

import javax.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

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

    private List<User> mockedUserList = new ArrayList<>();

    @Before
    public void initMockData() {

        mockedUserList.add(new UserBuilder().build());
    }

    @Test
    public void whenRegisterUserShouldCallService() throws UserException, ParkingException {
        RegistrationForm form = new RegistrationForm();
        form.setAccount(new Account("fullName", "username", "passwrod"));
        form.setParking(null);
    }

    @Test
    public void whenDisplayUsersShouldCallService() throws ApplicationException {
        adminController.displayUsers(mock(HttpServletRequest.class));
        then(adminService).should(times(1)).getUsers();
    }

    @Test
    public void whenDisplayUsersShouldReturnUsersFromService() throws ApplicationException {
        given(adminService.getUsers()).willReturn(mockedUserList);
        User returnUser = adminController.displayUsers(mock(HttpServletRequest.class)).get(0);
        assertThat(returnUser.getUsername(), is(mockedUserList.get(0).getUsername()));
    }

    @Test
    public void whenEditUserShouldCallService() throws ApplicationException{
        EditUserForm editUserForm = new EditUserForm();
        editUserForm.setAccount(new AccountBuilder().build());
        adminController.editUser(editUserForm,mock(HttpServletRequest.class));
    }
}
