package parking.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.UserException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private UserService userService;

    @Mock
    private ParkingService parkingService;

    private Account mockedAccount;

    @Before
    public void initMock() {
        mockedAccount = new Account("Name Surname", "username", "******");
    }

    @Test
    public void registerMustBeDefineAndAcceptRegisterObject() throws NoSuchMethodException {
        assertEquals(RegistrationService.class.getMethod("registerUser", Account.class, ParkingLot.class).getName(), "registerUser");
    }

    @Test
    public void whenRegistreationSuccessShouldReturnAccount() throws UserException {
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);

        assertTrue(Account.class.isInstance(registrationService.registerUser(mockedAccount, null)));
    }
}
