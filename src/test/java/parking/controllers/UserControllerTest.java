package parking.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.request.ChangePassword;
import parking.beans.request.EditUserForm;
import parking.beans.request.LoginForm;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.service.AdminService;
import parking.service.UserService;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.BDDMockito.when;
import static org.mockito.Matchers.eq;
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
    private LoginForm mockedLoginForm;
    @Mock
    private UserService userService;
    @Mock
    private ExceptionHandler exceptionHandler;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private AdminService adminService;
    @Mock
    private LogRepository logRepository;
    @Mock
    private Account mockedUser;
    @Mock
    private EditUserForm mockedEditUserForm;

    @Before
    public void initMock() throws ApplicationException {
        mockedLoginForm.setUsername("username");
        mockedLoginForm.setPassword("password");
        mockedLoginForm.setRemember(true);

        mockedEditUserForm.setFullName("Full Name");
        mockedEditUserForm.setPassword("password");

        mockedUser.setUsername(mockedLoginForm.getUsername());
        mockedUser.setPassword(mockedLoginForm.getPassword());

        Cookie ck1 = new Cookie("cookie", "aaaa");
        Cookie ck2 = new Cookie("othercookie", "bbbb");
        when(servletRequest.getSession()).thenReturn(session);
        when(servletRequest.getCookies()).thenReturn(new Cookie[]{ck1, ck2});

        when(exceptionHandler.handleException(ExceptionMessage.NOT_LOGGED, servletRequest)).thenReturn(new ApplicationException("message"));
    }

    @Test
    public void whenLoggingShouldCallServiceMethod() throws ApplicationException {
        controller.login(mockedLoginForm, servletRequest);

        verify(userService, times(1)).login(mockedLoginForm.getUsername(), mockedLoginForm.getPassword(), mockedLoginForm.getRemember(), servletRequest);
    }

    @Test
    public void whenLogOutShouldCallServiceMethod() throws ApplicationException {
        given(accountRepository.findByUsername(mockedLoginForm.getUsername())).willReturn(mockedUser);
        controller.logout(mockedLoginForm.getUsername(), mockedLoginForm.getPassword(), session, principal, servletRequest);

        verify(userService, times(1)).deleteCookies(mockedLoginForm.getUsername(), mockedLoginForm.getPassword());
    }

    @Test
    public void whenGettingProfileShouldCallServiceMethod() throws ApplicationException {
        controller.profile(servletRequest);

        verify(userService, times(1)).getCurrentUserProfile(servletRequest);
    }

    @Test(expected = ApplicationException.class)
    public void whenGettingProfileCanThrowException() throws ApplicationException {

        when(userService.getCurrentUserProfile(eq(servletRequest))).thenThrow(new ApplicationException(""));
        controller.profile(servletRequest);
    }

    @Test
    public void whenEditingProfileShouldCallService() throws ApplicationException, MessagingException {
        given(userService.getCurrentUser(servletRequest)).willReturn(mockedUser);
        controller.editUser(mockedEditUserForm, servletRequest);
    }
}
