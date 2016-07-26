package parking.repositories;


import parking.beans.document.Account;
import parking.beans.document.Log;
import parking.beans.document.LogMetaData;
import parking.utils.ActionType;

import java.util.Date;
import java.util.List;

public interface CustomLogRepository {
    public void insertActionLog(ActionType actionType, Account targetUser, Integer lotNumber, Date from, Date to, LogMetaData metaData, Account user, String channel);

    public List<Log> findDailyConfirmations(ActionType actionType, Date date);
}
