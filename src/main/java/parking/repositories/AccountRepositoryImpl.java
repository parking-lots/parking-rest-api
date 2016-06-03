package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.Car;
import parking.beans.document.ParkingLot;
import parking.helper.ProfileHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AccountRepositoryImpl implements CustomAccountRepository {

    private final MongoOperations operations;

    @Autowired
    public AccountRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public LotsRepository lotsRepository;

    @Override
    public void editAccount(Account account) {
        Query searchQuery = new Query(Criteria.where("username").is(account.getUsername()));

        Update updateFields = new Update();

        if (account.getFullName() != null) {
            updateFields.set("fullName", account.getFullName());
        }
        if (account.getPassword() != null) {
            updateFields.set("password", (ProfileHelper.encryptPassword(account.getPassword())));
        }
        if (account.getEmail() != null) {
            updateFields.set("email", account.getEmail());
        }

        if (account.getCarList().size() > 0) {
            updateFields.set("carList", account.getCarList());
        }

        operations.findAndModify(searchQuery, updateFields, Account.class);
    }

    public void attachParking(Integer lotNumber, String username) {
        Query searchQuery = new Query(Criteria.where("username").is(username));

        Update updateFields = new Update();
        ParkingLot parking = lotsRepository.findByNumber(lotNumber);

        updateFields.set("parking", parking);
        operations.findAndModify(searchQuery, updateFields, Account.class);
    }

    public void removeParking(String username) {
        Query searchQuery = new Query(Criteria.where("username").is(username));
        Update updateFields = new Update();

        updateFields.unset("parking");
        operations.findAndModify(searchQuery, updateFields, Account.class);
    }
}
