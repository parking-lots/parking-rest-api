package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import parking.beans.document.Account;
import parking.beans.response.Profile;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService service;
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private SecurityContext mockSecurityContext;

    @Mock
    private Authentication authentication;

    private Account mockedUser;

    private static final String CURRENT_USER_NAME = "name";

    @Before
    public void initMock() {
        when(authentication.getName()).thenReturn(CURRENT_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedUser = new Account();
        mockedUser.setFullName("Name Surname");
        mockedUser.setUsername("nickname");
        mockedUser.setPassword("****");
        mockedUser.setFlor(-1);
        mockedUser.setParkingNumber(189);

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedUser);
    }


    @Test
    public void whenGetingCurrentUser() throws UserException{
        Profile profile = service.getCurrentUserProfile();
        assertEquals(profile.toString(), new Profile(mockedUser).toString());
    }
}
