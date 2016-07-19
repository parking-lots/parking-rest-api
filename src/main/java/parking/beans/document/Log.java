package parking.beans.document;

import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import parking.utils.ActionType;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Document(collection = "log")
public class Log {
    @NotNull
    private ActionType actionType;
    @DBRef
    private Account targetUser;
    private Integer lotNumber;
    private Date from;
    private Date to;
    private LogMetaData metaData;
    @DBRef
    private Account user;
    private String userAgent;
    private Date timestamp;

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public Account getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(Account targetUser) {
        this.targetUser = targetUser;
    }

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public LogMetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(LogMetaData metaData) {
        this.metaData = metaData;
    }

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
