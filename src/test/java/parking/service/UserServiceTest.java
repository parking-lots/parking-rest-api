package parking.service;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import parking.beans.document.Account;
import parking.beans.document.Role;
import parking.beans.request.ChangePassword;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.repositories.RoleRepository;

import java.util.HashMap;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
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

    private Account mockedUser;

    private HashMap<String, Role> mockedRoles = new HashMap<String, Role>();

    private static final String MOCKED_USER_NAME = "nickname";

    @Before
    public void initMock() {
        when(authentication.getName()).thenReturn(MOCKED_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedUser = new Account("Name Surname", "nickname", "****");
        mockedRoles.put(Role.ROLE_USER, new Role(Role.ROLE_USER));

        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(mockedUser);
    }


    @Test
    public void whenGetingCurrentUser() throws UserException{
        Profile profile = service.getCurrentUserProfile();
        assertEquals(profile.toString(), new Profile(mockedUser).toString());
    }

    @Test
    public void whenChangePasswordCallRepositoryWithNewPassword() throws UserException {
        ChangePassword changePassword = new ChangePassword();
        changePassword.setNewPassword("password");

        mockedUser.setPassword(changePassword.getNewPassword());
        service.changePassword(changePassword);

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
    public void whenTryCreateUserWithExistUsernameShouldThrowException() throws UserException {
        service.createUser(mockedUser);
    }

    @Test
    public void whenCreateUserWithNotExistUserNameShouldCallRepository() throws UserException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);

        service.createUser(mockedUser);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        Optional<ObjectId> objectId = Optional.ofNullable(captor.getValue().getId());

        assertTrue(objectId.isPresent());
    }

    @Test
    public void whenCreateUserShouldRetrunAccountOnSuccess() throws UserException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(accountRepository.insert(mockedUser)).willReturn(mockedUser);

        Account newAccount = service.createUser(mockedUser);

        assertTrue(Account.class.isInstance(newAccount));
    }

    @Test
    public void whenCreateUserPasswordShouldBeEncrypted() throws UserException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        service.createUser(mockedUser);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertEquals(mockedUser.getPassword(), captor.getValue().getPassword());
    }

    @Test
    public void whenCreateUserWithoutParkingShouldAssignUserRole() throws UserException {
        given(accountRepository.findByUsername(MOCKED_USER_NAME)).willReturn(null);
        given(roleRepository.findByName(Role.ROLE_USER)).willReturn(mockedRoles.get(Role.ROLE_USER));

        service.createUser(mockedUser);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).insert(captor.capture());

        assertTrue(captor.getValue().getRoles().size() > 0);
        assertEquals(mockedRoles.get(Role.ROLE_USER).getName(), captor.getValue().getRoles().get(0).getName());
    }

}
