package parking.beans.request;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.RegistrationForm;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class RegistrationFormTest {

    private static final String METHOD_GET = "get";
    private static final String METHOD_SET = "set";

    @InjectMocks
    private RegistrationForm registrationForm;

    ArrayList<Parameter<?>> parameters = new ArrayList<Parameter<?>>();

    @Before
    public void initMock() {
        parameters.add(new Parameter<Account>("account", Account.class));
        parameters.add(new Parameter<ParkingLot>("parking", ParkingLot.class));
    }

    @Test
    public void checkMethods() throws NoSuchMethodException {
        Method[] methods = RegistrationForm.class.getMethods();

        for(Parameter parameter: parameters) {
            Method currentMethod = RegistrationForm.class.getMethod(getMethodName(parameter.getName(), METHOD_SET), parameter.getType());
            assertEquals(currentMethod.getName(), getMethodName(parameter.getName(), METHOD_SET));

            currentMethod = RegistrationForm.class.getMethod(getMethodName(parameter.getName(), METHOD_GET));
            assertEquals(currentMethod.getName(), getMethodName(parameter.getName(), METHOD_GET));
        }
    }

    private String getMethodName(String parameterName, String type) {
        return type + parameterName.substring(0,1).toUpperCase() + parameterName.substring(1);

    }
}

class Parameter<Type> {
    private String name;
    private Class<Type> type;

    public Parameter(String name, Class<Type> type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class<Type> getType() {
        return type;
    }

    public void setType(Class<Type> type) {
        this.type = type;
    }
}
