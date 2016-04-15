package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.Application;
import parking.beans.document.Account;
import parking.beans.response.Profile;
import parking.beans.response.User;
import parking.exceptions.ApplicationException;
import parking.exceptions.UserException;
import parking.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getUsers() {
        List<User> userList = new ArrayList<User>();

        Account account = accountRepository.findByUsername("lina.ramanauskaite");

        User user = new User(account);
      //  user.setFullName("Jane Jones");
      //  user.setUsername("importantDriver");
       // user.setRole("owner");
       // user.setParkingNo(888);
        userList.add(user);

//        user = new User();
//        user.setFullName("Tom Rogers");
//        user.setUsername("importantDriver2");
//        user.setRole("owner");
//        userList.add(user);
//
//        user.setFullName("Jim Johnson");
//        user.setUsername("importantDriver3");
//        user.setRole("owner");
//        user.setParkingNo(999);
//        userList.add(user);


        return userList;
    }
}
