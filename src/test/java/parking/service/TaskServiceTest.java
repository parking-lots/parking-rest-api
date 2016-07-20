package parking.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.Log;
import parking.exceptions.ApplicationException;
import parking.repositories.LogRepository;
import parking.utils.ActionType;

import javax.servlet.http.HttpServletRequest;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskServiceTest {
    @InjectMocks
    private TaskService service;
    @Mock
    private LogRepository logRepository;
    @Mock
    private MailService mailService;
    @Mock
    private HttpServletRequest request;

    private Account mockedAdmin = new Account();

    @Test
    public void whenNewUsersRegisteredShouldSendEmail() throws ApplicationException {
        List<Log> logRecords = new ArrayList<>();
        Log log = new Log();
        log.setActionType(ActionType.EMAIL_CONFIRMED);
        logRecords.add(log);
        mockedAdmin.setEmail("name.surname@swedbank.lt");

        given(logRepository.findDailyConfirmations(any(ActionType.class),any(Date.class))).willReturn(logRecords);
        service.notifyAboutNewUsers(request);
        verify(mailService).sendEmail(any(String.class), any(String.class), any(String.class));
    }

    @Test
    public void whenNoUsersRegisteredShoulNotSendEmail() throws ApplicationException{
        List<Log> logRecords = new ArrayList<>();
        given(logRepository.findDailyConfirmations(any(ActionType.class),any(Date.class))).willReturn(logRecords);
        service.notifyAboutNewUsers(request);
        verify(mailService, times(0)).sendEmail(any(String.class), any(String.class), any(String.class));

    }
}
