package parking.repositories;

import com.mongodb.BasicDBObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.utils.ActionType;

import java.util.Date;

public class LogRepositoryImpl implements CustomLogRepository {
    private final MongoOperations operations;

    @Autowired
    public LogRepositoryImpl(MongoOperations operations) {

        this.operations = operations;
    }

    public void insertActionLog(ActionType actionType, Account targetUser, Integer lotNumber, Date from, Date to, LogMetaData metaData, Account user, String channel) {
        BasicDBObject dbObject = new BasicDBObject();

        dbObject.put("actionType", actionType);
        dbObject.put("targetUser", targetUser);
        dbObject.put("lotNumber", lotNumber);
        dbObject.put("from", from);
        dbObject.put("to", to);

        if (metaData != null) {
            dbObject.put("metadata", metaData);
        }

        dbObject.put("userAgent", channel);
        dbObject.put("timestamp", new Date());

        operations.insert(dbObject, "log");
    }

    ;
}
