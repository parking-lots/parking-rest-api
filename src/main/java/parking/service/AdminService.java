package parking.service;

import org.springframework.stereotype.Service;
import parking.Application;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    public List<User> getUsers() {
        List<User> userList = new ArrayList<User>();

        User user = new User();
        user.setFullName("Jane Jones");
        user.setUsername("importantDriver");
        user.setRole("owner");
        user.setParkingNo(888);
        userList.add(user);

        user = new User();
        user.setFullName("Tom Rogers");
        user.setUsername("importantDriver2");
        user.setRole("owner");
        userList.add(user);

        user.setFullName("Jim Johnson");
        user.setUsername("importantDriver3");
        user.setRole("owner");
        user.setParkingNo(999);
        userList.add(user);


        return userList;
    }
}
