package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Log;


public interface LogRepository extends MongoRepository<Log, String>, CustomLogRepository {
}
