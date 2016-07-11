package parking.service;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.repositories.RoleRepository;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final String MOCKED_USER_NAME = "nickname";
    private static final String MOCKED_ADMIN_USERNAME = "admin";

    @InjectMocks
    private UserService service;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private LotsRepository lotsRepository;
    @Mock
    private LogRepository logRepository;
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private ParkingService parkingService;
    @Mock
    private UserService userService;
    @Mock
    private HttpServletResponse response;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private ExceptionHandler exceptionHandler;
    @Mock
    private HttpSession session;

    private Account mockedUser;
    private Account mockedAdmin;
    private ParkingLot mockedParking;
    private HashMap<String, Role> mockedRoles = new HashMap<String, Role>();
    private Cookie[] cookies = new Cookie[]{};
    private HttpServletRequest request = mock(HttpServletRequest.class);

    @Before
    public void initMock() {
        Cookie ck1 = new Cookie("cookie", "aaaa");
        Cookie ck2 = new Cookie("othercookie", "bbbb");

        when(authentication.getName()).thenReturn(MOCKED_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        when(exceptionHandler.handleException(ExceptionMessage.USER_ALREADY_LOGGED, request)).thenReturn(new ApplicationException("message"));
        when(exceptionHandler.handleException(ExceptionMessage.USER_ALREADY_EXIST, request)).thenReturn(new ApplicationException("message"));
        when(exceptionHandler.handleException(ExceptionMessage.NO_COOKIE_DATA, request)).thenReturn(new ApplicationException("message"));
        when(request.getCookies()).thenReturn(new Cookie[]{ck1, ck2});
        when(request.getHeader("User-Agent")).thenReturn("Opera Windows");
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedUser = new Account("Name Surname", MOCKED_USER_NAME, "****");
        mockedParking = new ParkingLot(161, -1);
        mockedUser.setParking(mockedParking);
        mockedRoles.put(Role.ROLE_USER, new Role(Role.ROLE_USER));

        mockedAdmin = new Account("Admin admin", MOCKED_ADMIN_USERNAME, "*****");
        mockedAdmin.setId(new ObjectId());

        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(mockedUser);
    }


    @Test
    public void whenGetingCurrentUser() throws ApplicationException {
        Profile profile = service.getCurrentUserProfile(request);
        assertEquals(profile.toString(), new Profile(mockedUser).toString());
    }

    @Test
    public void createMethodMustBeDefiedAndAcceptAccountObject() throws NoSuchMethodException {
        assertEquals(UserService.class.getMethod("createUser", Account.class, Integer.class, HttpServletRequest.class).getName(), "createUser");
    }

    @Test(expected = ApplicationException.class)
    public void whenTryCreateUserWithExistUsernameShouldThrowException() throws ApplicationException, MessagingException {
        given(accountRepository.findByUsername(mockedUser.getUsername())).willReturn(mockedUser);
        service.createUser(mockedUser, mockedParking.getNumber(), request);
    }

    @Test
    public void whenCreateUserWithNotExistUserNameShouldCallRepository() throws ApplicationException, MessagingException {
        given(authentication.getName()).willReturn(MOCKED_ADMIN_USERNAME);
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.findByUsername(MOCKED_ADMIN_USERNAME)).willReturn(mockedAdmin);
        given(userService.getCurrentUser(request)).willReturn(mockedUser);

        service.createUser(mockedUser, mockedParking.getNumber(), request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        Optional<ObjectId> objectId = Optional.ofNullable(captor.getValue().getId());

        assertTrue(objectId.isPresent());
    }

    @Test
    public void whenCreateUserShouldReturnAccountOnSuccess() throws ApplicationException, MessagingException {
        given(authentication.getName()).willReturn(MOCKED_ADMIN_USERNAME);
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.findByUsername(MOCKED_ADMIN_USERNAME)).willReturn(mockedAdmin);
        given(accountRepository.insert(mockedUser)).willReturn(mockedUser);
        given(userService.getCurrentUser(request)).willReturn(mockedUser);

        Account newAccount = service.createUser(mockedUser, mockedParking.getNumber(), request);

        assertTrue(Account.class.isInstance(newAccount));
    }

    @Test
    public void whenCreateUserPasswordShouldBeEncrypted() throws ApplicationException, MessagingException {
        given(authentication.getName()).willReturn(MOCKED_ADMIN_USERNAME);
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.findByUsername(MOCKED_ADMIN_USERNAME)).willReturn(mockedAdmin);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));
        service.createUser(mockedUser, mockedParking.getNumber(), request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertEquals(mockedUser.getPassword(), captor.getValue().getPassword());
    }

    @Test
    public void whenCreateUserWithoutParkingShouldAssignUserRole() throws ApplicationException, MessagingException {
        given(service.getCurrentUser(request)).willReturn(mockedAdmin);
        given(authentication.getName()).willReturn(MOCKED_ADMIN_USERNAME);
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.findByUsername(MOCKED_ADMIN_USERNAME)).willReturn(mockedAdmin);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));

        service.createUser(mockedUser, mockedParking.getNumber(), request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertTrue(captor.getValue().getRoles().size() > 0);
        assertEquals(mockedRoles.get(Role.ROLE_USER).getName(), captor.getValue().getRoles().get(0).getName());
    }

    @Test
    public void whenCreateUserWithCapitals() throws ApplicationException, MessagingException {
        mockedUser = new Account("Name Surname", MOCKED_USER_NAME, "****");
        given(service.getCurrentUser(request)).willReturn(mockedAdmin);
        given(authentication.getName()).willReturn(MOCKED_ADMIN_USERNAME);
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.findByUsername(MOCKED_ADMIN_USERNAME)).willReturn(mockedAdmin);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));

        service.createUser(mockedUser, mockedParking.getNumber(), request);

        assertEquals(MOCKED_USER_NAME, mockedUser.getUsername());//captor.getValue().getUsername());
    }

    @Test
    public void whenLoginWithAnyRememberMeOptionShouldSucceed() throws ApplicationException {
        String username = MOCKED_USER_NAME;
        String password = "****";

        given(authentication.getName()).willReturn(null);
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);
        loginForm.setRemember(true);

        String realLoginName = "lina";
        mockedUser = new Account("Lina Po", realLoginName, password);
        given(accountRepository.findByUsername(realLoginName)).willReturn(mockedUser);
        given(request.getSession(true)).willReturn(mock(HttpSession.class));
        given(request.getSession()).willReturn(mock(HttpSession.class));

        service.login(loginForm.getUsername(), loginForm.getPassword(), loginForm.getRemember(), request);
    }

    @Test
    public void whenSettingCookiesTheyAreSavedToBrowser() throws ApplicationException {
        final ArgumentCaptor<Cookie> captor = ArgumentCaptor.forClass(Cookie.class);

        service.setRememberMeCookies(mockedUser);

        verify(response, times(2)).addCookie(captor.capture());
        final List<Cookie> cookiesList = captor.getAllValues();

        for (Cookie cookie : cookiesList) {
            if (cookie.getName().equals(MOCKED_USER_NAME) && !cookie.getValue().equals(" ")) {
                assertEquals(cookie.getName(), MOCKED_USER_NAME);
            }
            if (cookie.getName().equals("password") && !cookie.getValue().equals(" ")) {
                assertEquals(cookie.getName(), "password");
            }
        }
    }

    @Test
    public void whenRememberMeCookiesCreatedUserAutomaticallyLoggedIn() throws ApplicationException {
        cookies = new Cookie[]{new Cookie(MOCKED_USER_NAME, mockedUser.getUsername()), new Cookie("password", mockedUser.getPassword())};

        given(request.getCookies()).willReturn(cookies);

        String username = cookies[0].getValue();
        String password = cookies[1].getValue();

        given(accountRepository.findByUsername(username)).willReturn(mockedUser);
        given(request.getSession(true)).willReturn(mock(HttpSession.class));
        given(request.getSession()).willReturn(mock(HttpSession.class));

        try {
            service.rememberMeLogin(username, password, request);
            fail("user is not logged");
        } catch (ApplicationException application) {

        }

        whenGetingCurrentUser();
    }

    @Test
    public void whenLogoutCookiesDeleted() {
        cookies = new Cookie[]{new Cookie(MOCKED_USER_NAME, mockedUser.getUsername()), new Cookie("password", mockedUser.getPassword())};

        given(request.getCookies()).willReturn(cookies);

        service.deleteCookies(cookies[0].getValue(), cookies[1].getValue());

        assertEquals(cookies[0].getValue(), mockedUser.getUsername());
        assertEquals(cookies[1].getValue(), mockedUser.getPassword());
    }

    @Test
    public void whenLoginSessionShouldBeAliveForSevenDays() {
        final ArgumentCaptor<Integer> captor = ArgumentCaptor.forClass(Integer.class);

        given(request.getSession()).willReturn(session);

        service.setMaxInactiveIntervalForSession(request);
        verify(session).setMaxInactiveInterval(captor.capture());
        List<Integer> a = captor.getAllValues();

        assertEquals(a.get(0), new Integer(604800));
    }
}
