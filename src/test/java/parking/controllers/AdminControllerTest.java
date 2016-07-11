package parking.controllers;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.request.RegistrationForm;
import parking.beans.response.User;
import parking.builders.AccountBuilder;
import parking.builders.UserBuilder;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;
import parking.service.AdminService;
import parking.service.RegistrationService;
import parking.utils.ParkingType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;
    @Mock
    private AdminService adminService;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private Account mockedAccount;

    private List<User> mockedUserList = new ArrayList<>();

    @Before
    public void initMockData() {
        mockedAccount.setUsername("username");
        mockedAccount.setPassword("password");
        mockedUserList.add(new UserBuilder().build());
    }

    @Test
    public void createUserMustBeMethod() throws NoSuchMethodException {
        assertEquals(AdminController.class.getMethod("createUser", RegistrationForm.class,
                HttpServletRequest.class).getName(), "createUser");
    }

    @Test
    public void whenCreatingUserShouldReturnNewAccount() throws ApplicationException, MessagingException {
        RegistrationForm form = new RegistrationForm();
        form.setAccount(new Account("fullName", "username", "passwrod"));
        form.setNumber(null);

        when(registrationService.registerUser(any(Account.class), any(Integer.class), eq(httpRequest))).thenReturn(mockedAccount);

        adminController.createUser(form, httpRequest);

        verify(registrationService, times(1)).registerUser(any(Account.class), any(Integer.class), eq(httpRequest));
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
    public void whenEditUserShouldCallService() throws ApplicationException, MessagingException {
        EditUserForm editUserForm = new EditUserForm();
        adminController.editUser(editUserForm, "username", mock(HttpServletRequest.class));
    }

    @Test
    public void whenDeleteUserCallService() throws ApplicationException {

        adminController.deleteUser("username", httpRequest);
        verify(adminService, times(1)).deleteUser(eq("username"), eq(httpRequest));
    }

    @Test
    public void whenGettingParkingsCallService() {
        adminController.getParkings(any(ParkingType.class));
        verify(adminService, times(1)).getParkings(any(ParkingType.class));
    }
}
