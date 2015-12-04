package parking.repositories;

import parking.beans.request.parkingNumberRequest;
import parking.beans.request.setUnusedRequest;
import parking.beans.response.ParkingLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;


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
    public List<ParkingLot> searchAllFields(final String userName) {
        Query searchQuery = new Query();

        Date currentDate = new Date();

        searchQuery.addCriteria(Criteria.where("freeTill").gte(currentDate));
        searchQuery.addCriteria(Criteria.where("freeFrom").gte(currentDate));
        searchQuery.addCriteria(new Criteria().orOperator(
                Criteria.where("currentlyUsed").is(null),
                Criteria.where("currentlyUsed").is(userName))
        );
        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);

        List<ParkingLot> filteredLots =  lots.stream()
                .filter(val -> userName.equals(val.getCurrentlyUsed()))
                .collect(Collectors.toList());

        if (filteredLots.size() > 0) {
            return filteredLots;
        }

        return lots;
    }

    @Override
    public void freeOwnersParking(setUnusedRequest request) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));
        Update updateFields = new Update();
        updateFields.set("freeTill",request.getFreeTill());
        updateFields.set("freeFrom",request.getFreeFrom());
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void recallParking(parkingNumberRequest request) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));
        Update updateFields = new Update();
        updateFields.unset("freeTill");
        updateFields.unset("freeFrom");
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void reserve(parkingNumberRequest request, String userName) {
        Query searchQuery = new Query(Criteria.where("number").is(request.getNumber()));
        operations.updateFirst(searchQuery, Update.update("currentlyUsed", userName), ParkingLot.class);
    }

    @Override
    public Integer getParkingNumberByUser(String name) {
        return null; //TODO implement
    }
}