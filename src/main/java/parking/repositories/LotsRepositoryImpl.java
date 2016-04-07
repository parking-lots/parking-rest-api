package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.helper.ToolHelper;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

        searchQuery.addCriteria(Criteria.where("freeTill").gte(currentDate));
        searchQuery.addCriteria(Criteria.where("freeFrom").lte(currentDate));
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
        //Update updateFields = new Update();
        //updateFields.set("freeTill",ToolHelper.formatDate(request.getFreeTill()));
        //updateFields.set("freeFrom",ToolHelper.formatDate(request.getFreeFrom()));
        //operations.updateFirst(searchQuery, updateFields, ParkingLot.class);

        ParkingLot insertObject = new ParkingLot();

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        insertObject.setNumber(request.getNumber());
        insertObject.setFreeFrom(request.getFreeFrom());
        insertObject.setFreeTill(request.getFreeTill());
        insertObject.setFloor(lots.get(0).getFloor());
        insertObject.setOwner(lots.get(0).getOwner());
        operations.insert(insertObject, "ParkingLot");

    }

    @Override
    public void recallParking(ParkingNumberRequest request) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));
        Update updateFields = new Update();
        updateFields.unset("freeTill");
        updateFields.unset("freeFrom");
        //operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
      //  operations.update(searchQuery, updateFields, ParkingLot.class);

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