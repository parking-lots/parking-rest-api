package parking.service;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;

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

    private Account mockedAccount;
    private ParkingLot mockedParking;

    @Before
    public void initMock() {
        mockedAccount = new Account("Name Surname", "username", "******");
        mockedParking = new ParkingLot(161, -2);
    }

    @Test
    public void registerMustBeDefineAndAcceptRegisterObject() throws NoSuchMethodException {
        assertEquals(RegistrationService.class.getMethod("registerUser", Account.class, ParkingLot.class).getName(), "registerUser");
    }

    @Test
    public void whenRegistreationSuccessShouldReturnAccount() throws UserException, ParkingException {
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);
        given(parkingService.createLot(mockedParking)).willReturn(mockedParking);

        assertTrue(Account.class.isInstance(registrationService.registerUser(mockedAccount, mockedParking)));
    }

    @Test
    public void whenRegisterUserShouldCallCreateParkingMethod() throws UserException, ParkingException {
        given(parkingService.createLot(mockedParking)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, mockedParking);
        verify(parkingService).createLot(mockedParking);
    }

    @Test
    public void whenRegisterUserShouldCallAttachParkingMethod() throws UserException, ParkingException {
        given(parkingService.createLot(mockedParking)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);
        registrationService.registerUser(mockedAccount, mockedParking);

        verify(userService).attachParking(mockedAccount, mockedParking.getNumber());
    }

    @Test
    public void whenRegisterUserShouldCallSetOwnerMethod() throws UserException, ParkingException {
        given(parkingService.createLot(mockedParking)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, mockedParking);

        verify(parkingService).setOwner(mockedAccount, mockedParking);
    }

    @Test
    public void whenRegisterUsesWithoutParkingShoulNotCallAttachAndSetOwnerMethods() throws UserException, ParkingException {
        given(userService.createUser(mockedAccount)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, null);

        verify(parkingService, never()).createLot(mockedParking);
        verify(parkingService, never()).setOwner(mockedAccount, mockedParking);
        verify(userService, never()).attachParking(mockedAccount, mockedParking.getNumber());
    }
}
