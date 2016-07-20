package parking.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import parking.beans.document.Account;
import parking.beans.document.Log;
import parking.beans.document.LogMetaData;
import parking.beans.document.ParkingLot;
import parking.helper.ToolHelper;
import parking.utils.ActionType;
import parking.utils.EliminateDateTimestamp;

import java.util.*;

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

    public List<Log> findDailyConfirmations(ActionType actionType, Date date) {

        Query searchQuery = new Query();
        searchQuery.addCriteria(Criteria.where("actionType").is(actionType.toString()));

        Calendar startOfDay = new EliminateDateTimestamp().formatDateForDatabase(date);

        //due to limitations of Mongodb only one criteria with timestamp can be added
        searchQuery.addCriteria(Criteria.where("timestamp").gte(startOfDay.getTime()));

        return operations.find(searchQuery, Log.class);
    }
}
