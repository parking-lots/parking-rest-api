package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import parking.beans.document.Account;

import java.util.List;

/**
 * Created by Lina on 15/04/16.
 */
@Component
public class UserRepositoryImpl implements CustomAdminRepository {

    private final MongoOperations operations;

    @Autowired
    public UserRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    public List<Account> getAllUsernames() {
        Query searchQuery = new Query();
        searchQuery.fields().include("username");
        return operations.findAll(Account.class);
    }
}
