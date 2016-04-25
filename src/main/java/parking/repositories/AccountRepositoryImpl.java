package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;

/**
 * Created by Lina on 25/04/16.
 */
public class AccountRepositoryImpl implements CustomAccountRepository {

    private final MongoOperations operations;

    @Autowired
    public AccountRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public void editAccount(Account request) {
        Query searchQuery = new Query(Criteria.where("username").is(request.getUsername()));

        Update updateFields = new Update();

        if(request.getUsername() != null){
        updateFields.set("username", request.getUsername());}
        if(request.getFullName() != null){
        updateFields.set("fullName", request.getFullName());}
        if(request.getPassword()!= null){
        updateFields.set("password", request.getPassword());}
        if(request.getEmail() != null){
        updateFields.set("email", request.getEmail());}
        if(request.getCarList() != null){
        updateFields.set("carList", request.getCarList());}

        operations.findAndModify(searchQuery, updateFields, Account.class);
    }
}
