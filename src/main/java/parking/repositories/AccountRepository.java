package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String>, CustomAccountRepository {
    public Account findByUsername(String username);

    public Account findByConfirmationKey(String confirmationKey);

    public List<Account> findAll();

    public Long deleteByUsername(String username);
}