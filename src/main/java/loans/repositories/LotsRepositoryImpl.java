package loans.repositories;

import loans.beans.response.ParkingLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class LotsRepositoryImpl implements CustomLostRepository {

    private static final int MAX_LIST_SIZE = 100;
    private final MongoOperations operations;

    @Autowired
    public LotsRepositoryImpl(MongoOperations operations) {
        this.operations = operations;
    }

    @Override
    public List<ParkingLot> searchAllFields() {
        Query searchQuery = new Query();
        Criteria searchCriteria = new Criteria();

        Date currentDate = new Date();

        searchQuery.addCriteria(searchCriteria.where("freeTill").gte(currentDate));
        searchQuery.addCriteria(searchCriteria.where("freeFrom").lte(currentDate));

        return operations.find(searchQuery, ParkingLot.class);
    }
}
