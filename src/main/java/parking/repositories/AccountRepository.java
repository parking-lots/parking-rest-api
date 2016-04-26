package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Account;

import java.util.List;

public interface AccountRepository extends MongoRepository<Account, String>, CustomAccountRepository {
    public Account findByUsername(String username);

    public List<Account> findAll();
}