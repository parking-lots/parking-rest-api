package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;
import parking.beans.request.EditUserForm;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.helper.ProfileHelper;
import parking.utils.ActionType;

import javax.servlet.http.HttpServletRequest;
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
    private LogRepository logRepository;

    @Autowired
    public ExceptionHandler exceptionHandler;

    @Autowired
    public RoleRepository roleRepository;

    @Override
    public void editAccount(EditUserForm newAccount, Account oldAccount, String username) {
        Query searchQuery = new Query(Criteria.where("username").is(username));

        Update updateFields = new Update();

        if (!newAccount.getFullName().equals(oldAccount.getFullName())) {
            updateFields.set("fullName", newAccount.getFullName());
        }
        //if password received is not null - means it is changed
        if (newAccount.getPassword() != null) {
            updateFields.set("password", (ProfileHelper.encryptPassword(newAccount.getPassword())));
        }

        if (newAccount.getEmail() == null) {
            updateFields.set("email", null);
        } else if (!newAccount.getEmail().equals(oldAccount.getEmail())) {
            updateFields.set("email", newAccount.getEmail());
        }

        if (newAccount.getCarRegNoList() == null || newAccount.getCarRegNoList().size() == 0) {
            updateFields.unset("carRegNoList");
        } else if (!oldAccount.getCarRegNoList().containsAll(newAccount.getCarRegNoList()) || !(newAccount.getCarRegNoList().containsAll(oldAccount.getCarRegNoList()))) {
            updateFields.set("carRegNoList", newAccount.getCarRegNoList());
        }

        operations.updateFirst(searchQuery, updateFields, Account.class);

    }

    public void attachParking(Integer lotNumber, String username, HttpServletRequest httpRequest) throws ApplicationException {
        Optional<ParkingLot> parkingLot = Optional.ofNullable(lotsRepository.findByNumber(lotNumber));
        if (Optional.ofNullable(parkingLot.get().getOwner()).isPresent()) {
            throw exceptionHandler.handleException(ExceptionMessage.PARKING_OWNED_BY_ANOTHER, httpRequest);

        }
        Query searchQuery = new Query(Criteria.where("username").is(username));

        Update updateFields = new Update();
        ParkingLot parking = lotsRepository.findByNumber(lotNumber);

        updateFields.set("parking", parking);

        Role ownerRole = roleRepository.findByName(Role.ROLE_OWNER);
        List<Account> selectedAccounts = operations.find(searchQuery, Account.class);

        if (!selectedAccounts.get(0).getRoles().contains(ownerRole)) {
            updateFields.addToSet("roles", ownerRole);
        }

        operations.findAndModify(searchQuery, updateFields, Account.class);

        lotsRepository.setParkingOwner(lotNumber, username);
    }

    public void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException {
        Query searchQuery = new Query(Criteria.where("username").is(username));
        List<Account> accounts = operations.find(searchQuery, Account.class);

        if (accounts.size() == 0) {
            throw exceptionHandler.handleException(ExceptionMessage.USER_NOT_FOUND, httpRequest);
        }

        Update updateFields = new Update();
        updateFields.unset("parking");

        Role ownerRole = roleRepository.findByName(Role.ROLE_OWNER);
        updateFields.pull("roles", ownerRole);

        operations.findAndModify(searchQuery, updateFields, Account.class);
    }
}
