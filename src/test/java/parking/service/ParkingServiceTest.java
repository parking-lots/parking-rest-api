package parking.service;

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
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.document.ParkingLot;
import parking.builders.LotsBuilder;
import parking.beans.document.Account;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParkingServiceTest {

    @InjectMocks
    private ParkingService service;
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


    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private Account mockedAccount;
    private static final String CURRENT_USER_NAME = "name";

    @Before
    public void initMock() throws UserException {
        when(authentication.getName()).thenReturn(CURRENT_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedAccount= new Account();
        mockedAccount.setParkingNumber(105);
        mockedAccount.setUsername("username");
        when(userService.getCurrentUser()).thenReturn(mockedAccount);

        mockedParkingLotList.add(new LotsBuilder().number(100).build());
        mockedParkingLotList.add(new LotsBuilder().number(101).build());
        mockedParkingLotList.add(new LotsBuilder().number(103).build());
        mockedParkingLotList.add(new LotsBuilder().number(104).build());
    }

    @Test
    public void whereGetAvailableReturnAllAvailableItems() throws UserException {
        given(lotsRepository.searchAllFields(mockedAccount)).willReturn(mockedParkingLotList);

        assert(service.getAvailable()).containsAll(mockedParkingLotList);
    }

    @Test
    public void whenUserPlacedReturnOnlyPlacedParking() throws UserException {
        List<ParkingLot> placedParking = mockedParkingLotList;
        placedParking.add(new LotsBuilder().number(100).user(mockedAccount).build());

        given(lotsRepository.searchAllFields(mockedAccount)).willReturn(placedParking);

        assertTrue(service.getAvailable().size() == 1
            && service.getAvailable().get(0).equals(placedParking.get(4)));
    }

    @Test
    public void whenOwnerFreeUpParkingLot() {

        SetUnusedRequest request = new SetUnusedRequest();
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);
        service.freeOwnersParking(request);

        ArgumentCaptor captor = ArgumentCaptor.forClass(SetUnusedRequest.class);
        verify(lotsRepository).freeOwnersParking((SetUnusedRequest) captor.capture());

        SetUnusedRequest value = (SetUnusedRequest) captor.getValue();
        assertEquals(value.getNumber(), mockedAccount.getParkingNumber());
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssigned() {
        mockedAccount.setParkingNumber(null);
        SetUnusedRequest request = new SetUnusedRequest();

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.freeOwnersParking(request);
        verify(lotsRepository, never()).freeOwnersParking(request);
    }

    @Test
    public void whenOwnerRecallParkingLot() {
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking();

        ArgumentCaptor captor = ArgumentCaptor.forClass(ParkingNumberRequest.class);
        verify(lotsRepository).recallParking((ParkingNumberRequest) captor.capture());

        ParkingNumberRequest value = (ParkingNumberRequest) captor.getValue();
        assertEquals(value.getNumber(), mockedAccount.getParkingNumber());
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssignedAndTryRecall() {

        ArgumentCaptor captor = ArgumentCaptor.forClass(ParkingNumberRequest.class);
        mockedAccount.setParkingNumber(null);
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking();
        verify(lotsRepository, never()).recallParking((ParkingNumberRequest) captor.capture());
    }

    @Test
    public void whenUserReserveParkingLot() throws UserException {
        ParkingNumberRequest request = new ParkingNumberRequest();

        service.reserve(request);
        verify(lotsRepository).reserve(request, mockedAccount);
    }

    @Test
    public void whenCancelReservation() throws UserException {
       service.cancelRezervation();
       verify(lotsRepository).cancelReservation(mockedAccount);
    }
}
