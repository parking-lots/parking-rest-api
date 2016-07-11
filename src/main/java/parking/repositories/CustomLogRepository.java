package parking.repositories;


import parking.beans.document.Account;
import parking.beans.document.LogMetaData;
import parking.utils.ActionType;

import java.util.Date;

public interface CustomLogRepository {
    public void insertActionLog(ActionType actionType, Account targetUser, Integer lotNumber, Date from, Date to, LogMetaData metaData, Account user, String channel);
}
