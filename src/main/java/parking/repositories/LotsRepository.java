package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;

import java.util.List;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
    public ParkingLot findByNumber(Integer number);
    public List<ParkingLot> findAll();
}
