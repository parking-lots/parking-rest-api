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
import parking.helper.ToolHelper;
import parking.utils.ParkingType;

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
    public void freeOwnersParking(Integer lotNumber, Date freeFrom, Date freeTill) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        AvailablePeriod availablePeriod = new AvailablePeriod();
        availablePeriod.setFreeFrom(freeFrom);
        availablePeriod.setFreeTill(freeTill);

        updateFields.addToSet("availablePeriods", availablePeriod);
        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

//    @Override
//    public void recallParking(Integer lotNumber) {
//        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
//        Update updateFields = new Update();
//        updateFields.unset("availablePeriods");
//        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
//    }

    @Override
    public void recallParking(Integer lotNumber, Date freeFrom, Date freeTill) {
        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        Update updateFields = new Update();

        if(freeFrom == null && freeTill == null) {
            updateFields.unset("availablePeriods");
        }
        else {
            searchQuery.addCriteria(new Criteria().andOperator(
                    Criteria.where("availablePeriods.freeFrom").is(freeFrom),
                    Criteria.where("availablePeriods.freeTill").is(freeTill)
            ));

            BasicDBObject obj = new BasicDBObject();
            obj.put("freeFrom", freeFrom);
            obj.put("freeTill", freeTill);
            updateFields.pull("availablePeriods", obj);
        }

        operations.updateFirst(searchQuery, updateFields, ParkingLot.class);
    }

    @Override
    public void reserve(Integer lotNumber, Account user) {
        Query searchQuery = new Query();
        Date currentDate = ToolHelper.getCurrentDate();
        searchQuery.addCriteria(new Criteria()
                .andOperator(
                        Criteria.where("number").is(lotNumber),
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

    @Override
    public List<ParkingLot> findParking(ParkingType type) {
        Query searchQuery;

        if(type.equals(ParkingType.unassigned)) {
            searchQuery = new Query(Criteria.where("owner").is(null));
        }
        else {
            searchQuery = new Query();
        }

        List<ParkingLot> lots = operations.find(searchQuery, ParkingLot.class);
        return lots;
    }
}