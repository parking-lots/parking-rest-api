package parking.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.request.ChangePassword;
import parking.exceptions.ApplicationException;
import parking.exceptions.UserException;
import parking.service.UserService;

import javax.servlet.http.*;
import java.security.Principal;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    private UserService userService;

    @Mock
    HttpServletRequest servletRequest;

    @Mock
    Principal principal;

    @Test(expected = UserException.class)
    public void whenNotLoggedInShouldThrowException() throws ApplicationException {

        controller.profile(servletRequest, null);
    }

    @Test
    public void whenLoggedInShouldCallServiceMethod() throws ApplicationException {
        controller.profile(servletRequest, principal);

        verify(userService, times(1)).getCurrentUserProfile();
    }

    @Test
    public void whenChangePasswordShouldCallServiceMethod() throws ApplicationException {
        ChangePassword changePassword = new ChangePassword();
        controller.changePassword(changePassword, servletRequest);

        verify(userService, times(1)).changePassword(changePassword, servletRequest);
    }
}
