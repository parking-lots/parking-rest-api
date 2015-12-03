package loans.repositories;

import loans.beans.response.ParkingLot;

import org.springframework.data.mongodb.repository.MongoRepository;


public interface LotsRepository extends MongoRepository<ParkingLot, String>, CustomLotsRepository {
}
