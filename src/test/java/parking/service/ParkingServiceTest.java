package parking.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.ParkingLot;
import parking.builders.LotsBuilder;
import parking.repositories.Account;
import parking.repositories.AccountRepository;
import parking.repositories.LotsRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    private Authentication authentication;


    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private static final String CURRENT_USER_NAME = "name";

    @Before
    public void initMock() {

        authentication = new Authentication() {
            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return CURRENT_USER_NAME;
            }
        };

        when(mockSecurityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        mockedParkingLotList.add(new LotsBuilder().number(100).owner("Name Surname").build());
        mockedParkingLotList.add(new LotsBuilder().number(101).owner("Name Surname2").build());
        mockedParkingLotList.add(new LotsBuilder().number(103).owner("Name Surname3").build());
        mockedParkingLotList.add(new LotsBuilder().number(104).owner("Name Surname4").build());
    }

    @Test
    public void whereGetAvailableReturnAllAvailableItems() {
        given(lotsRepository.searchAllFields(CURRENT_USER_NAME)).willReturn(mockedParkingLotList);

        assert(service.getAvailable()).containsAll(mockedParkingLotList);
    }

    @Test
    public void whenOwnerFreeUpParkingLot() {
        Account account = new Account();
        account.setParkingNumber(105);

        SetUnusedRequest request = new SetUnusedRequest();
        given(accountRepository.findByUsername(CURRENT_USER_NAME)).willReturn(account);

        service.freeOwnersParking(request);

        ArgumentCaptor captor = ArgumentCaptor.forClass(SetUnusedRequest.class);
        verify(lotsRepository).freeOwnersParking((SetUnusedRequest) captor.capture());

        SetUnusedRequest value = (SetUnusedRequest) captor.getValue();
        assertEquals(value.getNumber(), account.getParkingNumber());


    }
}
