package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.Role;
import parking.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AdminServiceTest {
    @InjectMocks
    private AdminService service;

    @Mock
    private AccountRepository accountRepository;

    private List<Account> mockedAccountList = new ArrayList<>();

    @Before
    public void initData() {
        Account account = new Account();
        account.setFullName("Tom Tomsson");
        account.setUsername("tom111");
        mockedAccountList.add(account);

        given(accountRepository.findAll()).willReturn(mockedAccountList);
    }

    @Test
    public void whenGettingAllUsers(){
        assertEquals(mockedAccountList.get(0).getUsername(), service.getUsers().get(0).getUsername());
    }
}
