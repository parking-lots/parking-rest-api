package loans.repositories;

import loans.beans.request.parkingNumberRequest;
import loans.beans.request.setUnusedRequest;
import loans.beans.response.ParkingLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class LotsRepositoryImpl implements CustomLostRepository {

    private final MongoOperations operations;

    @Autowired
    public LotsRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public List<ParkingLot> searchAllFields() {
        return operations.findAll(ParkingLot.class);
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
}