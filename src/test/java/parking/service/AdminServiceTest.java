package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;
import parking.beans.request.ChangePassword;
import parking.beans.request.EditUserForm;
import parking.exceptions.ApplicationException;
import parking.repositories.AccountRepository;
import parking.repositories.LogRepository;
import parking.repositories.LotsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
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

    private Account mockedAccount;
    private ParkingLot mockedParkingLot;
    private List<Account> mockedAccountList = new ArrayList<>();
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

        given(accountRepository.findAll()).willReturn(mockedAccountList);
    }

    @Test
    public void whenGettingAllUsers() {
        assertEquals(mockedAccountList.get(0).getUsername(), service.getUsers().get(0).getUsername());
    }

    @Test
    public void whenEditUser() throws ApplicationException {
        given(userService.getCurrentUser(request)).willReturn(mockedAccount);
        given(accountRepository.findByUsername("username")).willReturn(mockedAccount);
        service.editUser(editUserForm, mockedAccount.getUsername(), request);
        verify(accountRepository).editAccount(any(EditUserForm.class), any(String.class));
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
