package parking.controllers;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.request.RegistrationForm;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;
import parking.service.RegistrationService;

import java.lang.reflect.Method;

import static junit.framework.TestCase.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private RegistrationService registrationService;

    @Mock
    private AccountRepository accountRepository;

    @Test
    public void createUserMustBeMethod() throws NoSuchMethodException {
        String methodName = "createUser";
        Method method = AdminController.class.getMethod(methodName, RegistrationForm.class);

        assertEquals(method.getName(), methodName);
    }

    @Test
    public void whenRegisterUserShouldCallService() throws UserException {
        RegistrationForm form = new RegistrationForm();
        form.setAccount(new Account("fullName", "username", "passwrod"));
        form.setParking(null);

        given(registrationService.registerUser(form.getAccount(), form.getParking())).willReturn(form.getAccount());

        adminController.createUser(form);
        verify(registrationService).registerUser(form.getAccount(), form.getParking());
    }
}
