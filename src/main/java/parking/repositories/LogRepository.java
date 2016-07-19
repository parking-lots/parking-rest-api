package parking.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import parking.beans.document.Log;

import java.util.List;


public interface LogRepository extends MongoRepository<Log, String>, CustomLogRepository {
    public List<Log> findAll();

}
