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
import parking.beans.request.RecallParking;
import parking.beans.request.SetUnusedRequest;
import parking.builders.LotsBuilder;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.EliminateDateTimestamp;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    private LogRepository logRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private SecurityContext mockSecurityContext;
    @Mock
    private Authentication authentication;
    @Mock
    private UserService userService;
    @Mock
    private ParkingService parkingService;
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

        when(exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, httpRequest)).thenReturn(new ApplicationException("message"));
        when(exceptionHandler.handleException(ExceptionMessage.PARKING_ALREADY_EXISTS, httpRequest)).thenReturn(new ApplicationException("message"));

        SecurityContextHolder.setContext(mockSecurityContext);

        mockedAccount = new Account();
        mockedAccount.setUsername(CURRENT_USER_NAME);

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
        Date from = new Date();
        Date to = new Date();

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);
        service.freeOwnersParking(mockedParkingLot.getOwner(), mockedParkingLot.getNumber(), from, to, httpRequest);

        verify(lotsRepository).freeOwnersParking(
                eq(mockedAccount.getParking().getNumber()),
                eq(from),
                eq(to),
                eq(httpRequest));
    }

    @Test
    public void whenCustomerDoesNotHaveParkingAssigned() throws ApplicationException {
        mockedAccount.setParking(null);
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.freeOwnersParking(null, mockedParkingLot.getNumber(), new Date(), new Date(), httpRequest);
        verify(lotsRepository, never()).freeOwnersParking(200, new Date(), new Date(), httpRequest);
    }

    @Test
    public void whenOwnerRecallParkingLot() throws ApplicationException {
        RecallParking recallParking = new RecallParking();
        recallParking.setFreeFrom(mockDate);
        recallParking.setFreeTill(mockDate);

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking(recallParking.getFreeFrom(), recallParking.getFreeTill(), httpRequest);

        verify(lotsRepository).recallParking(mockedAccount.getParking().getNumber(), recallParking.getFreeFrom(), recallParking.getFreeTill());

        assertEquals(mockedAccount.getParking().getNumber(), mockedAccount.getParking().getNumber());
    }

    @Test
    public void whenOwnerRecallParkingWithSingleDates() throws ApplicationException {
        Date singleDate = new Date();
        List<Date> dateList = new ArrayList<>();
        dateList.add(singleDate);

        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(mockedAccount);

        service.recallParking(dateList, httpRequest);

        verify(lotsRepository).recallParking(mockedAccount.getParking().getNumber(), dateList.get(0), httpRequest);
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
        given(lotsRepository.findByNumber(mockedParkingLot.getNumber())).willReturn(mockedParkingLot);

        service.reserve(mockedParkingLot.getNumber(), httpRequest);
        verify(lotsRepository).reserve(mockedParkingLot.getNumber(), mockedAccount, httpRequest);
    }

    @Test(expected = ApplicationException.class)
    public void whenUserReserveNonExistingParkingLot() throws ApplicationException {
        given(lotsRepository.findByNumber(mockedParkingLot.getNumber())).willReturn(null);

        service.reserve(mockedParkingLot.getNumber(), httpRequest);
        verify(lotsRepository, never()).reserve(mockedParkingLot.getNumber(), mockedAccount, httpRequest);
    }

    @Test
    public void whenCancelReservation() throws ApplicationException {
        given(lotsRepository.findByUser(mockedAccount)).willReturn(mockedParkingLot);
        when(parkingService.getCurrentUserName()).thenReturn(mockedAccount.getUsername());
        when(parkingService.getParkingByNumber(mockedParkingLot.getNumber(), httpRequest)).thenReturn(mockedParkingLot);
        given(accountRepository.findByUsername(mockedAccount.getUsername())).willReturn(mockedAccount);

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