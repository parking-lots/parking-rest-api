package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.response.User;
import parking.repositories.AccountRepository;
import parking.repositories.CustomAdminRepository;
import parking.repositories.UserRepositoryImpl;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CustomAdminRepository customAdminRepository;

    public List<User> getUsers() {
        List<Account> accountList = customAdminRepository.getAllUsernames();

        List<User> userList = new ArrayList<User>();

        User user;

        Account account;
        for (Account a:accountList) {
            account = accountRepository.findByUsername(a.getUsername());
            user = new User(account);
            userList.add(user);
        }

        return userList;
    }
}
