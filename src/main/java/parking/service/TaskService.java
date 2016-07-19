package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;

import javax.servlet.http.HttpServletRequest;

public class TaskService {

    @Autowired
    private MailService mailService;
    @Autowired
    private ExceptionHandler exceptionHandler;

    public void notifyAboutNewUsers(HttpServletRequest httpRequest) throws ApplicationException {
        String subject = "";
        String message = "";

        try {
            mailService.sendEmail("lina.po@outlook.com", subject, message);
        } catch (Exception e) {
            throw exceptionHandler.handleException(ExceptionMessage.COULD_NOT_SEND_EMAIL, httpRequest);
        }
    }
}
