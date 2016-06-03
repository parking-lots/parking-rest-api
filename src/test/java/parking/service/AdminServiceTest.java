package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;
import parking.beans.request.ChangePassword;
import parking.beans.request.EditUserForm;
import parking.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
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
        mockedAccount.setUsername("tom111");
        mockedAccount.setParking(mockedParkingLot);
        mockedAccountList.add(mockedAccount);

        given(accountRepository.findAll()).willReturn(mockedAccountList);
    }

    @Test
    public void whenGettingAllUsers(){
        assertEquals(mockedAccountList.get(0).getUsername(), service.getUsers().get(0).getUsername());
    }

    @Test
    public void whenEditUser(){
        service.editUser(mockedAccount, request);
        verify(accountRepository).editAccount(any(Account.class));
    }

    @Test
    public void whenDeleteUser(){
        service.deleteUser(mockedAccount.getUsername());
        verify(accountRepository).deleteByUsername(any(String.class));
    }

    @Test
    public void whenAttachParking(){
        service.attachParking(mockedAccount.getParking().getNumber(), mockedAccount.getUsername());
    }

    @Test
    public void whenRemoveParkingFromUser(){
        service.removeParkingFromUser(mockedAccount.getUsername());
    }
}
