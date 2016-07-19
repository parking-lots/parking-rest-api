package parking.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.service.TaskService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = "/task")
public class TaskController {
    @Autowired
    private TaskService taskService;

    @RequestMapping(value = "/notice", method = RequestMethod.POST)
    public void notify(HttpServletRequest request) throws ApplicationException {
        taskService.notifyAboutNewUsers(request);
    }
}
