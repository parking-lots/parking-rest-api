package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.helper.ProfileHelper;

public class AccountRepositoryImpl implements CustomAccountRepository {

    private final MongoOperations operations;

    @Autowired
    public AccountRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public LotsRepository lotsRepository;

    @Override
    public void editAccount(EditUserForm newAccount, String username) {
        Query searchQuery = new Query(Criteria.where("username").is(username));

        Update updateFields = new Update();

        if (newAccount.getFullName() != null) {
            updateFields.set("fullName", newAccount.getFullName());
        }
        if (newAccount.getPassword() != null) {
            updateFields.set("password", (ProfileHelper.encryptPassword(newAccount.getPassword())));
        }
        if (newAccount.getEmail() != null) {
            updateFields.set("email", newAccount.getEmail());
        }

        if (newAccount.getCarRegNoList().size() > 0) {
            updateFields.set("carRegNoList", newAccount.getCarRegNoList());
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

    public void detachParking(int lotNumber) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        updateFields.unset("owner");
        operations.findAndModify(searchQuery, updateFields, ParkingLot.class);
    }
}
