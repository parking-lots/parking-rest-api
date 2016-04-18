package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.response.User;
import parking.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getUsers() {
        List<User> userList = new ArrayList<>();

        User user;
        for (Account a: accountRepository.findAll()){
            user = new User(a);
            userList.add(user);
        }

        return userList;
    }
}
