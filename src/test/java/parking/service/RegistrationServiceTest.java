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

    private Account mockedAccount;
    private ParkingLot mockedParking;
    private Integer mockedParkingLotNumber;

    @Before
    public void initMock() {
        mockedAccount = new Account("Name Surname", "username", "******");
        mockedParking = new ParkingLot(161, -2);
//        mockedParkingLotNumber = mockedParking.getNumber();
    }

    @Test
    public void registerMustBeDefineAndAcceptRegisterObject() throws NoSuchMethodException {
        assertEquals(RegistrationService.class.getMethod("registerUser", Account.class, Integer.class, HttpServletRequest.class).getName(), "registerUser");
    }

    @Test
    public void whenRegistrationSuccessShouldReturnAccount() throws ApplicationException {
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);
        given(parkingService.createLot(mockedParking, request)).willReturn(mockedParking);

        assertTrue(Account.class.isInstance(registrationService.registerUser(mockedAccount, mockedParkingLotNumber, request)));
    }

    @Test
    public void whenRegisterUserShouldCallAttachParkingMethod() throws ApplicationException {
        given(parkingService.createLot(mockedParking, request)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);
        registrationService.registerUser(mockedAccount, mockedParking.getNumber(), request);

        verify(userService).attachParking(mockedAccount, mockedParking.getNumber(), request);
    }

    @Test
    public void whenRegisterUserShouldCallSetOwnerMethod() throws ApplicationException {
        given(parkingService.createLot(mockedParking, request)).willReturn(mockedParking);
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, mockedParking.getNumber(), request);

        ParkingLot parkingLot = parkingService.getParkingByNumber(mockedParking.getNumber(), request);

        verify(parkingService).setOwner(mockedAccount, parkingLot);
    }

    @Test
    public void whenRegisterUsesWithoutParkingShouldNotCallAttachAndSetOwnerMethods() throws ApplicationException {
        given(userService.createUser(mockedAccount, request)).willReturn(mockedAccount);

        registrationService.registerUser(mockedAccount, null, request);

        verify(parkingService, never()).createLot(mockedParking, request);
        verify(parkingService, never()).setOwner(mockedAccount, mockedParking);
        verify(userService, never()).attachParking(mockedAccount, mockedParking.getNumber(), request);
    }
}
