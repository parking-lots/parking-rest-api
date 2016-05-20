package parking.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import parking.beans.request.ChangePassword;
import parking.beans.response.Response;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.service.UserService;

import javax.servlet.http.*;
import java.security.Principal;

import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.Mockito.times;

@RunWith(MockitoJUnitRunner.class)
public class UserControllerTest {

    @InjectMocks
    private UserController controller;

    @Mock
    HttpServletRequest servletRequest;
    @Mock
    HttpSession session;
    @Mock
    Principal principal;
    @Mock
    private UserService userService;
    @Mock
    private ExceptionHandler exceptionHandler;

    @Before
    public void initMock() throws ApplicationException {
        Cookie ck1 = new Cookie("cookie", "aaaa");
        Cookie ck2 = new Cookie("othercookie", "bbbb");
        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getCookies()).thenReturn(new Cookie[]{ck1, ck2});
        when(exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, servletRequest)).thenReturn(new ApplicationException("message"));
    }

    @Test(expected = ApplicationException.class)
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
