package parking.helper;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import parking.exceptions.ApplicationException;
import parking.exceptions.ParkingException;
import parking.exceptions.UserException;

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

        switch (message) {
            case USER_ALREADY_LOGGED:
            case USER_ALREADY_EXIST:
            case USER_NOT_FOUND:
            case WRONG_CREDENTIALS:
            case NOT_LOGGED:
            case NO_COOKIE_DATA:
                return new UserException(getMessage(message.getMsg(), request));
            case PARKING_ALREADY_EXISTS:
            case PARKING_DOES_NOT_EXIST:
            case PARKING_OWNED_BY_ANOTHER:
            case PARKING_NOT_AVAILABLE:
            case END_DATE_IN_THE_PAST:
            case START_DATE_LATER_THAN_END_DATE:
            case DATE_IN_THE_PAST:
            case DATE_DOES_NOT_EXIST:
            case OVERLAPPING_PERIOD:
            case DUBLICATE_DATES:
            case DOES_NOT_HAVE_PARKING:
            case EMPTY_CAR_REG_NO:
                return new ParkingException(getMessage(message.getMsg(), request));
            default:
                return (ApplicationException) new Exception(message.getMsg());
        }
    }

}
