package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.Log;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.beans.response.LogResponse;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;
import parking.utils.ActionType;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminService service;

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private EditUserForm editUserForm;
    @Mock
    private UserService userService;
    @Mock
    private LogRepository logRepository;
    @Mock
    private LotsRepository lotsRepository;
    @Mock
    private MailService mailService;

    private Account mockedAccount;
    private ParkingLot mockedParkingLot;
    private Log mockedLog;
    private List<Account> mockedAccountList = new ArrayList<>();
    private List<Log> mockedLogList = new ArrayList<>();
    private HttpServletRequest request = mock(HttpServletRequest.class);


    @Before
    public void initData() {
        mockedParkingLot = new ParkingLot();
        mockedParkingLot.setNumber(200);
        mockedParkingLot.setFloor(-1);
        mockedParkingLot.setOwner(mockedAccount);

        mockedAccount = new Account();
        mockedAccount.setFullName("Tom Tomsson");
        mockedAccount.setUsername("username");
        mockedAccount.setParking(mockedParkingLot);
        mockedAccount.setEmail("name@mail.com");
        mockedAccountList.add(mockedAccount);

        mockedLog = new Log();
        mockedLog.setActionType(ActionType.SHARE);
        mockedLog.setTargetUser(mockedAccount);
        mockedLog.setLotNumber(101);
        mockedLog.setFrom(new Date());
        mockedLog.setTo(new Date());
        mockedLog.setUser(mockedAccount);
        mockedLog.setTimestamp(new Date());
        mockedLogList.add(mockedLog);

        given(accountRepository.findAll()).willReturn(mockedAccountList);
        given(logRepository.findAll()).willReturn(mockedLogList);
    }

    @Test
    public void whenGettingAllUsers() {
        assertEquals(new User(mockedAccountList.get(0)).toString(), service.getUsers().get(0).toString());
    }

    @Test
    public void whenGettingLog() {
        assertEquals(new LogResponse(mockedLogList.get(0)).toString(), service.getLog().get(0).toString());
    }

    @Test
    public void whenEditUser() throws ApplicationException, MessagingException {
        given(userService.getCurrentUser(request)).willReturn(mockedAccount);
        given(accountRepository.findByUsername("username")).willReturn(mockedAccount);
        service.editUser(editUserForm, mockedAccount.getUsername(), request);
        verify(accountRepository).editAccount(any(EditUserForm.class), any(Account.class), any(String.class), any(HttpServletRequest.class));
    }

    @Test
    public void whenDeleteUser() throws ApplicationException {
        given(userService.getCurrentUser(request)).willReturn(mockedAccount);
        given(accountRepository.findByUsername("username")).willReturn(mockedAccount);
        service.deleteUser("username", request);
        verify(accountRepository).deleteByUsername(any(String.class));
    }

    @Test
    public void whenDetachParkingFromUser() throws ApplicationException {
        given(accountRepository.findByUsername(mockedAccount.getUsername())).willReturn(mockedAccount);
        service.detachParking(mockedAccount.getUsername(), request);
    }
}
