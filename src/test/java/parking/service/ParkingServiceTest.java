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
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallParking;
import parking.beans.request.SetUnusedRequest;
import parking.builders.LotsBuilder;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;
import parking.utils.EliminateDateTimestamp;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private Account mockedAccount;
    private ParkingLot mockedParkingLot;
    private EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
    private Date mockDate = eliminateDateTimestamp.formatDateForDatabase(new Date()).getTime();

    @Before
    public void initMock() throws ApplicationException {
        when(authentication.getName()).thenReturn(CURRENT_USER_NAME);
        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);

        when(exceptionHandler.handleException(ExceptionMessage.PARKING_DID_NOT_EXIST, httpRequest)).thenReturn(new ApplicationException("message"));
        when(exceptionHandler.handleException(ExceptionMessage.PARKING_ALREADY_EXISTS, httpRequest)).thenReturn(new ApplicationException("message"));

        SecurityContextHolder.setContext(mockSecurityContext);

        mockedAccount = new Account();
        mockedAccount.setUsername("username");

        when(userService.getCurrentUser(httpRequest)).thenReturn(mockedAccount);

        mockedParkingLotList.add(new LotsBuilder().number(100).build());
        mockedParkingLotList.add(new LotsBuilder().number(101).build());
        mockedParkingLotList.add(new LotsBuilder().number(103).build());
        mockedParkingLotList.add(new LotsBuilder().number(104).build());

        LinkedList<AvailablePeriod> availablePeriods = new LinkedList<>();
        //2016-11-12 - 2016-11-20
        AvailablePeriod availablePeriod = new AvailablePeriod(new Date(1478908800000L), new Date(1479600000000L));
        availablePeriods.add(availablePeriod);

        mockedParkingLot = new ParkingLot(161, -2);
        mockedParkingLot.setOwner(mockedAccount);
        mockedParkingLot.setAvailablePeriods(availablePeriods);
        mockedAccount.setParking(mockedParkingLot);
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
        request.setFreeFrom(new Date());
        request.setFreeTill(new Date());

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);
        service.freeOwnersParking(request.getFreeFrom(), request.getFreeTill(), httpRequest);

        verify(lotsRepository).freeOwnersParking(
                eq(mockedAccount.getParking().getNumber()),
                eq(request.getFreeFrom()),
                eq(request.getFreeTill()));
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssigned() throws ApplicationException {
        mockedAccount.setParking(null);
        SetUnusedRequest request = new SetUnusedRequest();

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.freeOwnersParking(request.getFreeFrom(), request.getFreeTill(), httpRequest);
        verify(lotsRepository, never()).freeOwnersParking(200, request.getFreeFrom(), request.getFreeTill());
    }

    @Test
    public void whenOwnerRecallParkingLot() throws ApplicationException {
        RecallParking recallParking = new RecallParking();
        recallParking.setFreeFrom(mockDate);
        recallParking.setFreeTill(mockDate);

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking(recallParking.getFreeFrom(), recallParking.getFreeTill(), httpRequest);

       // ArgumentCaptor captor = ArgumentCaptor.forClass(ParkingNumberRequest.class);
        verify(lotsRepository).recallParking(mockedAccount.getParking().getNumber(), recallParking.getFreeFrom(), recallParking.getFreeTill());

       // ParkingNumberRequest value = (ParkingNumberRequest) captor.getValue();
       //
        assertEquals(mockedAccount.getParking().getNumber(), mockedAccount.getParking().getNumber());
    }

    @Test
    public void whenOwnerRecallParkingWithSingleDates() throws ApplicationException {
        Date singleDate = new Date(1479168000000L); //2016-11-15
        List<Date> dateList = new ArrayList<>();
        dateList.add(singleDate);

        RecallParking recallParking = new RecallParking();
        recallParking.setAvailableDates(dateList);

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking(recallParking.getAvailableDates(), httpRequest);

        verify(lotsRepository).recallParking(mockedAccount.getParking().getNumber(), recallParking.getAvailableDates().get(0));
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssignedAndTryRecall() throws ApplicationException {
        RecallParking recallParking = new RecallParking();

        mockedAccount.setParking(null);
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking(recallParking.getFreeFrom(), recallParking.getFreeTill(), httpRequest);
        verify(lotsRepository, never()).recallParking(200, recallParking.getFreeFrom(), recallParking.getFreeTill());
    }

    @Test
    public void whenUserReserveParkingLot() throws ApplicationException {

        service.reserve(mockedAccount.getParking().getNumber(), httpRequest);
        verify(lotsRepository).reserve(mockedAccount.getParking().getNumber(), mockedAccount);
    }

    @Test
    public void whenCancelReservation() throws ApplicationException {
        service.cancelReservation(httpRequest);
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

        Optional<ObjectId> maybeObjectId = Optional.ofNullable(captor.getValue().getId());

        assertTrue(maybeObjectId.isPresent());
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