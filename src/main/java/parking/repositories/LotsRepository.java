package parking.repositories;

import parking.beans.document.ParkingLot;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
    public ParkingLot findByNumber(Integer number);
}
