package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.response.User;
import parking.repositories.AccountRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getUsers() {
       // List<User> userList = new ArrayList<>();

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());
//        for (Account account: accountRepository.findAll()){
//            userList.add(new User(account));
//        }

 //       return userList;
    }
}
