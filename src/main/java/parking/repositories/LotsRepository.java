package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Account;
import parking.beans.document.ParkingLot;

import java.util.List;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
    public ParkingLot findByNumber(Integer number);

    public ParkingLot findByUser(Account user);

    public List<ParkingLot> findAll();
}
