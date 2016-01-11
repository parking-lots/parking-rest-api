package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Account;

public interface AccountRepository extends MongoRepository<Account, String> {
    public Account findByUsername(String username);
}