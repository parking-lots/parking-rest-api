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

/**
 * Created by Lina on 25/04/16.
 */
public class AccountRepositoryImpl implements CustomAccountRepository {

    private final MongoOperations operations;

    @Autowired
    public AccountRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public LotsRepository lotsRepository;

    @Override
    public void editAccount(Account request) {
        Query searchQuery = new Query(Criteria.where("username").is(request.getUsername()));

        Update updateFields = new Update();

        if (request.getFullName() != null) {
            updateFields.set("fullName", request.getFullName());
        }
        if (request.getPassword() != null) {
            updateFields.set("password", (ProfileHelper.encryptPassword(request.getPassword())));
        }
        if (request.getEmail() != null) {
            updateFields.set("email", request.getEmail());
        }

        if (request.getCarList().size() > 0) {
            updateFields.set("carList", request.getCarList());
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
