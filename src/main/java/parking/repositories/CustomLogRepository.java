package parking.repositories;


import org.bson.types.ObjectId;
import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.utils.ActionType;

import java.util.Date;

public interface CustomLogRepository {
    public void insertActionLog(ActionType actionType, ObjectId targetUserId, Integer lotNumber, Date from, Date to, LogMetaData metaData, ObjectId userId, String channel);
}
