package loans.repositories;

import loans.beans.response.ParkingLot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;

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
        return operations.findAll(ParkingLot.class);
    }
}
