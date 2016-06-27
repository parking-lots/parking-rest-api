package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.EditUserForm;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.helper.ProfileHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AccountRepositoryImpl implements CustomAccountRepository {

    private final MongoOperations operations;

    @Autowired
    public AccountRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Autowired
    public LotsRepository lotsRepository;

    @Autowired
    public ExceptionHandler exceptionHandler;

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

    public void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("username").is(username));
        List<Account> accounts = operations.find(searchQuery, Account.class);

        if (accounts.size() == 0) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, httpRequest);
        }

        Update updateFields = new Update();
        updateFields.unset("parking");
        operations.findAndModify(searchQuery, updateFields, Account.class);
    }
}
