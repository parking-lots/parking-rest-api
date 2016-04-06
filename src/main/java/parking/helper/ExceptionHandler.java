package parking.helper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import parking.exceptions.ApplicationException;
import parking.exceptions.UserException;
import parking.exceptions.ParkingException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

@Component
public class ExceptionHandler {
    @Autowired
    private MessageSource messageSource;


    private String getMessage(String message, HttpServletRequest request) {
        return messageSource.getMessage(message, new Object[0], getLocale(request));
    }

    private Locale getLocale(HttpServletRequest request) {
        return new SessionLocaleResolver().resolveLocale(request);
    }

    public ApplicationException handleException(ExceptionMessage message, HttpServletRequest request) {

        switch (message){
            case USER_ALREADY_LOGGED:
            case USER_NOT_FOUND:
            case WRONG_CREDENTIALS:
            case NOT_LOGGED:
                return new UserException(getMessage(message.getMsg(), request));
            case PARKING_ALREADY_EXISTS:
            case PARKING_DID_NOT_EXIST:
            case PARKING_OWNED_BY_ANOTHER:
                return new ParkingException(getMessage(message.getMsg(), request));
            default:
                return null;
        }
    }

}
