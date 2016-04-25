package parking.repositories;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallSingleParking;
import parking.beans.request.SetUnusedRequest;
import parking.helper.ToolHelper;

import java.util.Date;
import java.util.List;

public class LotsRepositoryImpl implements CustomLotsRepository {

    private final MongoOperations operations;

    @Autowired
    public LotsRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public List<ParkingLot> searchAllFields(final Account user) {
        Query searchQuery = new Query();

        Date currentDate = ToolHelper.getCurrentDate();

        searchQuery.addCriteria(Criteria.where("availablePeriods.freeTill").gte(currentDate));
        searchQuery.addCriteria(Criteria.where("availablePeriods.freeFrom").lte(currentDate));
        searchQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("reserved").is(null),
                Criteria.where("user.$id").is(user.getId()))
        );
        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);
        return lots;
    }

    @Override
    public void freeOwnersParking(SetUnusedRequest request) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));

        Update updateFields = new Update();

        AvailablePeriod availablePeriod = new AvailablePeriod();
        availablePeriod.setFreeFrom(request.getFreeFrom());
        availablePeriod.setFreeTill(request.getFreeTill());

        updateFields.addToSet("availablePeriods", availablePeriod);
            operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void recallParking(ParkingNumberRequest request) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));
        Update updateFields = new Update();
        updateFields.unset("availablePeriods");
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void recallSingleParking(RecallSingleParking recallSingleParking) {
        Query searchQuery = new Query(Criteria.where("number").is(recallSingleParking.getNumber()));
        Update updateFields = new Update();

        searchQuery.addCriteria(new Criteria().andOperator(
                Criteria.where("availablePeriods.freeFrom").is(recallSingleParking.getFreeFrom()),
                Criteria.where("availablePeriods.freeTill").is(recallSingleParking.getFreeTill())
        ));

        BasicDBObject obj = new BasicDBObject();
        obj.put("freeFrom",recallSingleParking.getFreeFrom());
        obj.put("freeTill",recallSingleParking.getFreeTill());
        updateFields.pull("availablePeriods", obj);

        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void reserve(ParkingNumberRequest request, Account user) {
        Query searchQuery = new Query();
        Date currentDate = ToolHelper.getCurrentDate();
        searchQuery.addCriteria(new Criteria()
                .andOperator(
                        Criteria.where("number").is(request.getNumber()),
                        Criteria.where("reserved").is(null)
                ));
        Update updateFields = new Update();
        updateFields.set("user", user);
        updateFields.set("reserved", currentDate);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void cancelReservation(Account user) {
        Query searchQuery = new Query(Criteria.where("user.$id").is(user.getId()));
        Update updateFields = new Update();
        updateFields.unset("user");
        updateFields.unset("reserved");
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }
}