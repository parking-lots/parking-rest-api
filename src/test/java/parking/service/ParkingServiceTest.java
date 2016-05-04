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
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.builders.LotsBuilder;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParkingServiceTest {

    private static final String CURRENT_USER_NAME = "name";

    @InjectMocks
    private ParkingService service;

    @Mock
    HttpServletRequest httpRequest;
    @Mock
    private LotsRepository lotsRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserService userService;
    @Mock
    private ExceptionHandler exceptionHandler;

    @Mock
    private   HttpServletRequest httpServletRequest;

    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private Account mockedAccount;
    private ParkingLot mockedParkingLot;

    @Before
    public void initMock() throws ApplicationException {
        when(authentication.getName()).thenReturn(CURRENT_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        when(exceptionHandler.handleException(ExceptionMessage.PARKING_DID_NOT_EXIST, httpRequest)).thenReturn(new ApplicationException("message"));
        when(exceptionHandler.handleException(ExceptionMessage.PARKING_ALREADY_EXISTS, httpRequest)).thenReturn(new ApplicationException("message"));

        SecurityContextHolder.setContext(mockSecurityContext);

        mockedAccount = new Account();
        mockedAccount.setUsername("username");
        mockedAccount.setParking(new ParkingLot(100, -1));
        when(userService.getCurrentUser(httpRequest)).thenReturn(mockedAccount);

        mockedParkingLotList.add(new LotsBuilder().number(100).build());
        mockedParkingLotList.add(new LotsBuilder().number(101).build());
        mockedParkingLotList.add(new LotsBuilder().number(103).build());
        mockedParkingLotList.add(new LotsBuilder().number(104).build());

        mockedParkingLot = new ParkingLot(161, -2);
    }

    @Test
    public void whereGetAvailableReturnAllAvailableItems() throws ApplicationException {
        given(lotsRepository.searchAllFields(mockedAccount)).willReturn(mockedParkingLotList);

        assert (service.getAvailable(httpRequest)).containsAll(mockedParkingLotList);
    }

    @Test
    public void whenUserPlacedReturnOnlyPlacedParking() throws ApplicationException {
        List<ParkingLot> placedParking = mockedParkingLotList;
        placedParking.add(new LotsBuilder().number(100).user(mockedAccount).build());

        given(lotsRepository.searchAllFields(mockedAccount)).willReturn(placedParking);

        assertTrue(service.getAvailable(httpRequest).size() == 1
                && service.getAvailable(httpRequest).get(0).equals(placedParking.get(4)));
    }

    @Test
    public void whenOwnerFreeUpParkingLot() throws ApplicationException {

        SetUnusedRequest request = new SetUnusedRequest();
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);
        service.freeOwnersParking(request, httpServletRequest);

        ArgumentCaptor captor = ArgumentCaptor.forClass(SetUnusedRequest.class);
        verify(lotsRepository).freeOwnersParking((SetUnusedRequest) captor.capture());

        SetUnusedRequest value = (SetUnusedRequest) captor.getValue();
        assertEquals(value.getNumber(), mockedAccount.getParking().getNumber());
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssigned() throws ApplicationException {
        mockedAccount.setParking(null);
        SetUnusedRequest request = new SetUnusedRequest();

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.freeOwnersParking(request, httpServletRequest);
        verify(lotsRepository, never()).freeOwnersParking(request);
    }

    @Test
    public void whenOwnerRecallParkingLot() {
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking();

        ArgumentCaptor captor = ArgumentCaptor.forClass(ParkingNumberRequest.class);
        verify(lotsRepository).recallParking((ParkingNumberRequest) captor.capture());

        ParkingNumberRequest value = (ParkingNumberRequest) captor.getValue();
        assertEquals(value.getNumber(), mockedAccount.getParking().getNumber());
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssignedAndTryRecall() {

        ArgumentCaptor captor = ArgumentCaptor.forClass(ParkingNumberRequest.class);
        mockedAccount.setParking(null);
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking();
        verify(lotsRepository, never()).recallParking((ParkingNumberRequest) captor.capture());
    }

    @Test
    public void whenUserReserveParkingLot() throws ApplicationException {
        ParkingNumberRequest request = new ParkingNumberRequest();

        service.reserve(request, httpRequest);
        verify(lotsRepository).reserve(request, mockedAccount);
    }

    @Test
    public void whenCancelReservation() throws ApplicationException {
        service.cancelRezervation(httpRequest);
        verify(lotsRepository).cancelReservation(mockedAccount);
    }

    @Test
    public void creataLotShouldBeDefined() throws NoSuchMethodException {
        assertEquals(ParkingService.class.getMethod("createLot", ParkingLot.class, HttpServletRequest.class).getName(), "createLot");
    }

    @Test
    public void whenCreateLotShouldReturnParkingLotInstance() throws ApplicationException {
        given(lotsRepository.insert(mockedParkingLot)).willReturn(mockedParkingLot);
        given(lotsRepository.findByNumber(mockedParkingLot.getNumber())).willReturn(null);

        ParkingLot newParking = service.createLot(mockedParkingLot, httpRequest);

        assertTrue(ParkingLot.class.isInstance(newParking));
    }

    @Test
    public void whenCreateLotShouldCallRepository() throws ApplicationException {
        service.createLot(mockedParkingLot, httpRequest);
        verify(lotsRepository).insert(mockedParkingLot);
    }

    @Test
    public void whenCreateParkingShouldGenerateId() throws ApplicationException {
        service.createLot(mockedParkingLot, httpRequest);

        ArgumentCaptor<ParkingLot> captor = ArgumentCaptor.forClass(ParkingLot.class);
        verify(lotsRepository).insert(captor.capture());

        Optional<ObjectId> objectId = Optional.ofNullable(captor.getValue().getId());

        assertTrue(objectId.isPresent());
    }

    @Test(expected = ApplicationException.class)
    public void whenCreateAlreadyExistLotShoudThrowException() throws ApplicationException {
        given(lotsRepository.findByNumber(161)).willReturn(mockedParkingLot);

        service.createLot(mockedParkingLot, httpRequest);
    }

    @Test
    public void getParkingByNumberShouldBeDefined() throws NoSuchMethodException {
        assertEquals(ParkingService.class.getMethod("getParkingByNumber", Integer.class, HttpServletRequest.class).getName(), "getParkingByNumber");
    }

    @Test(expected = ApplicationException.class)
    public void whenGetNotExistParkingByNumberShouldThrowException() throws ApplicationException {
        given(lotsRepository.findByNumber(mockedParkingLot.getNumber())).willReturn(null);
        service.getParkingByNumber(mockedParkingLot.getNumber(), httpRequest);
        service.createLot(mockedParkingLot, httpRequest);
    }

    @Test
    public void whenSetOwnerSuccessShouldCallRepository() {
        service.setOwner(mockedAccount, mockedParkingLot);

        ArgumentCaptor<ParkingLot> captor = ArgumentCaptor.forClass(ParkingLot.class);
        verify(lotsRepository).save(captor.capture());

        assertEquals(captor.getValue().getOwner().getUsername(), mockedAccount.getUsername());
    }

}
