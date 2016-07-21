package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Log;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.repositories.LogRepository;
import parking.utils.ActionType;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private MailService mailService;
    @Autowired
    private ExceptionHandler exceptionHandler;
    @Autowired
    private LogRepository logRepository;

    public void notifyAboutNewUsers(HttpServletRequest httpRequest) throws ApplicationException {

        List<Log> logRecords = logRepository.findDailyConfirmations(ActionType.EMAIL_CONFIRMED, new Date());

        int numberOfUsers = logRecords.size();
        if (numberOfUsers == 0) {
            return;
        }

        String subject;
        String message;

        switch (numberOfUsers) {
            case 1:
                subject = "Today " + logRecords.size() + " new Parkinger user registered";
                message = "<p>Hello,</p><p>There is " + logRecords.size() + " new user registered at Parkinger waiting for your approval.</p>" +
                        "<p>Please, register user's car plate numbers and activate the account.</p>" +
                        "<p>You can check new users <a href=\"http://www.parkinger.net/admin\">here</a></p>";
                break;
            default:
                subject = "Today " + logRecords.size() + " new Parkinger users registered";
                message = "<p>Hello,</p><p>There are " + logRecords.size() + " new users registered at Parkinger waiting for your approval.</p>" +
                        "<p>Please, register their car plate numbers and activate their accounts.</p>" +
                        "<p>You can check new users <a href=\"http://www.parkinger.net/admin\">here</a></p>";
                break;
        }

        try {
            mailService.sendEmail("lina.po@outlook.com", subject, message);
        } catch (Exception e) {
            throw exceptionHandler.handleException(ExceptionMessage.COULD_NOT_SEND_EMAIL, httpRequest);
        }
    }

}
