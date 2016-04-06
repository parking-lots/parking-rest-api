package parking.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class ExceptionHandlerTest {
    @InjectMocks
    ExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MessageSource messageSource;

    @Test
    public void checkExceptionsSetup(){
        for (ExceptionMessage k: ExceptionMessage.values()){
            assertNotNull(exceptionHandler.handleException(k, request));
        }
    }
}
