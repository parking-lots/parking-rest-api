package parking.service;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;
import parking.beans.request.ChangePassword;
import parking.beans.request.LoginForm;
import parking.beans.response.Profile;
import parking.exceptions.ApplicationException;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.repositories.RoleRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private LotsRepository lotsRepository;

    @Mock
    private SecurityContext mockSecurityContext;

    @Mock
    private Authentication authentication;

    @Mock
    private ParkingService parkingService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private AuthenticationManager authenticationManager;

    private Account mockedUser;
    private ParkingLot mockedParking;

    private HashMap<String, Role> mockedRoles = new HashMap<String, Role>();

    private static final String MOCKED_USER_NAME = "nickname";

    @Before
    public void initMock() {
        when(authentication.getName()).thenReturn(MOCKED_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedUser = new Account("Name Surname", "nickname", "****");
        mockedParking = new ParkingLot(161, -1);
        mockedRoles.put(Role.ROLE_USER, new Role(Role.ROLE_USER));

        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(mockedUser);
    }


    @Test
    public void whenGetingCurrentUser() throws UserException{
        Profile profile = service.getCurrentUserProfile();
        assertEquals(profile.toString(), new Profile(mockedUser).toString());
    }

    @Test
    public void whenChangePasswordCallRepositoryWithNewPassword() throws ApplicationException {
        ChangePassword changePassword = new ChangePassword();
        changePassword.setNewPassword("password");

        mockedUser.setPassword(changePassword.getNewPassword());
        service.changePassword(changePassword, request);

        ArgumentCaptor captor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save((Account) captor.capture());

        Account value = (Account) captor.getValue();
        assertEquals(value.getPassword(), mockedUser.getPassword());
    }

    @Test
    public void createMethodMustBeDefiedAndAcceptAccountObject() throws NoSuchMethodException {
        assertEquals(UserService.class.getMethod("createUser", Account.class).getName(), "createUser");
    }
    @Test(expected = UserException.class)
    public void whenTryCreateUserWithExistUsernameShouldThrowException() throws ApplicationException {
        service.createUser(mockedUser, request);
    }

    @Test
    public void whenCreateUserWithNotExistUserNameShouldCallRepository() throws ApplicationException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);

        service.createUser(mockedUser, request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        Optional<ObjectId> objectId = Optional.ofNullable(captor.getValue().getId());

        assertTrue(objectId.isPresent());
    }

    @Test
    public void whenCreateUserShouldRetrunAccountOnSuccess() throws ApplicationException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.insert(mockedUser)).willReturn(mockedUser);

        Account newAccount = service.createUser(mockedUser, request);

        assertTrue(Account.class.isInstance(newAccount));
    }

    @Test
    public void whenCreateUserPasswordShouldBeEncrypted() throws ApplicationException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        service.createUser(mockedUser, request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertEquals(mockedUser.getPassword(), captor.getValue().getPassword());
    }

    @Test
    public void whenCreateUserWithoutParkingShouldAssignUserRole() throws ApplicationException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));

        service.createUser(mockedUser, request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertTrue(captor.getValue().getRoles().size() > 0);
        assertEquals(mockedRoles.get(Role.ROLE_USER).getName(), captor.getValue().getRoles().get(0).getName());
    }

    @Test
    public void whenCreateUserWithCapitals() throws ApplicationException {
        mockedUser = new Account("Name Surname", "NICKname", "****");

        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));

        service.createUser(mockedUser, request);

        //ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        //verify(accountRepository).insert(captor.capture());

        assertEquals("nickname", mockedUser.getUsername());//captor.getValue().getUsername());
    }

    @Test
    public void attachParkingMethodShouldBeDefined() throws NoSuchMethodException {
        assertEquals(UserService.class.getMethod("attachParking", Account.class, Integer.class).getName(), "attachParking");
    }

    @Test
    public void whenAttachParkingToUserSuccessShouldCallUdpdateServiceMethod() throws ApplicationException {
        given(parkingService.getParkingByNumber(161, request)).willReturn(mockedParking);
        service.attachParking(mockedUser, 161, request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        verify(accountRepository).save(captor.capture());

        assertTrue(captor.getValue().getParking().getNumber() == 161);
    }

    @Test(expected = ParkingException.class)
    public void whenAttachParkingWhichOwnedByAnotherUserShouldThrowException() throws ApplicationException {
        mockedParking.setOwner(new Account("Name surname", "name.surname", "******"));
        given(parkingService.getParkingByNumber(161, request)).willReturn(mockedParking);
        service.attachParking(mockedUser, 161, request);
    }

    @Test(expected = ParkingException.class)
    public void whenAttachParkingWhichDidNotExistShouldThrowException() throws ApplicationException {
        doThrow(new ParkingException("")).when(parkingService).getParkingByNumber(161, request);
        service.attachParking(mockedUser, 161, request);
    }

    @Test
    public void whenAttachParkinShoulAddOwnerRole() throws ApplicationException {
        given(parkingService.getParkingByNumber(161, request)).willReturn(mockedParking);
        given(roleRepository.findByName(Role.ROLE_OWNER)).willReturn(new Role(Role.ROLE_OWNER));

        service.attachParking(mockedUser, 161, request);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());

        assertEquals(captor.getValue().getRoles().get(0).getName(), Role.ROLE_OWNER);
    }

    @Test
    public void login() throws ApplicationException {
        String username = "Lina";
        String password = "****";

        given(authentication.getName()).willReturn(null);
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername(username);
        loginForm.setPassword(password);

        String realLoginName = "lina";
        mockedUser = new Account("Lina Po", realLoginName, password);
        given(accountRepository.findByUsername(realLoginName)).willReturn(mockedUser);
        given(request.getSession(true)).willReturn(mock(HttpSession.class));

        service.login(loginForm, request);

    }


}
