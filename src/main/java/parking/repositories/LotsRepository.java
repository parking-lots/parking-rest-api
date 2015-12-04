package parking.repositories;

import parking.beans.response.ParkingLot;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
}
