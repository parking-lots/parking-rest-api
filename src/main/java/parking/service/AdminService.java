package parking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import parking.beans.document.Account;
import parking.beans.response.User;
import parking.repositories.AccountRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private AccountRepository accountRepository;

    public List<User> getUsers() {

        return accountRepository.findAll().stream()
                .map(User::new)
                .collect(Collectors.toList());

    }

    public void editUser(Account account, HttpServletRequest request) {

        accountRepository.editAccount(account);
    }
}
