package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import parking.beans.document.Account;
import parking.beans.document.Log;
import parking.beans.document.LogMetaData;
import parking.utils.ActionType;

import java.util.Date;
import java.util.Optional;

public class LogRepositoryImpl implements CustomLogRepository {
    private final MongoOperations operations;

    @Autowired
    public LogRepositoryImpl(MongoOperations operations) {

        this.operations = operations;
    }

    public void insertActionLog(ActionType actionType, Account targetUser, Integer lotNumber, Date from, Date to, LogMetaData metaData, Optional<Account> user, String userAgent) {
        Log log = new Log();

        if (actionType != null)
            log.setActionType(actionType);

        if (targetUser != null)
            log.setTargetUser(targetUser);

        if (lotNumber != null)
            log.setLotNumber(lotNumber);

        if (from != null)
            log.setFrom(from);

        if (to != null)
            log.setTo(to);

        if (metaData != null)
            log.setMetaData(metaData);

        if (userAgent != null)
            log.setUserAgent(userAgent);

        if (user.isPresent())
            log.setUser(user.get());

        log.setTimestamp(new Date());

        operations.insert(log);
    }
}
