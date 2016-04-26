package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.ParkingLot;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
    public ParkingLot findByNumber(Integer number);
}
