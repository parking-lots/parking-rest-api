package parking.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationServiceTest {

    @InjectMocks
    private RegistrationService registrationService;

    @Mock
    private UserService userService;
    @Mock
    private ParkingService parkingService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private AccountRepository accountRepository;

    private Account mockedAccount;
    private ParkingLot mockedParking;

    @Before
    public void initMock() {
        mockedAccount = new Account("Name Surname", "username", "******");
        mockedParking = new ParkingLot(161, -2);
    }

    @Test
    public void registerMustBeDefineAndAcceptRegisterObject() throws NoSuchMethodException {
        assertEquals(RegistrationService.class.getMethod("registerUser", Account.class, Integer.class, HttpServletRequest.class).getName(), "registerUser");
    }

    @Test
    public void whenRegistreationSuccessShouldReturnAccount() throws ApplicationException {
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);
        given(parkingService.createLot(mockedParking, request)).willReturn(mockedParking);
        given(accountRepository.findByUsername(mockedAccount.getUsername())).willReturn(mockedAccount);

        assertTrue(Account.class.isInstance(registrationService.registerUser(mockedAccount, mockedParking.getNumber(), request)));

    }

    @Test
    public void whenRegisterUserShouldCallAttachParkingMethod() throws ApplicationException {
        given(parkingService.createLot(mockedParking, request)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, mockedParking.getNumber(), request);
        verify(accountRepository).attachParking(mockedParking.getNumber(), mockedAccount.getUsername(), request);
    }

    @Test
    public void whenRegisterUserWithoutParkingShouldNotCallAttachParking() throws ApplicationException {
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, null, request);

        verify(accountRepository, never()).attachParking(mockedParking.getNumber(), mockedAccount.getUsername(), request);
    }
}
