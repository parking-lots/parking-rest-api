package parking.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.exceptions.ApplicationException;
import parking.service.TaskService;

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class TaskControllerTest {

    @InjectMocks
    private TaskController controller;
    @Mock
    private HttpServletRequest request;
    @Mock
    private TaskService taskService;

    @Test
    public void whenNotifyShouldCallService() throws ApplicationException {
        controller.notifyAdmin(request);
        verify(taskService).notifyAboutNewUsers(request);
    }

}
