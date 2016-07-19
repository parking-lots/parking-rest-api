package parking.beans.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import parking.beans.document.Log;
import parking.utils.ActionType;

import java.util.Date;

public class LogResponse extends Response {
    private ActionType actionType;
    private String targetUser;

    private Integer lotNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date from;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date to;

    private String user;
    private String userAgent;
    private Date timestamp;

    public LogResponse(Log log) {
        this.actionType = log.getActionType();
        if (log.getTargetUser() != null)
            this.targetUser = log.getTargetUser().getUsername();
        this.lotNumber = log.getLotNumber();
        this.from = log.getFrom();
        this.to = log.getTo();
        this.user = log.getUser().getUsername();
        this.userAgent = log.getUserAgent();
        this.timestamp = log.getTimestamp();
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public void setTargetUser(String targetUser) {
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

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
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

    @Override
    public String toString() {
        return "LogResponse{" +
                "actionType=" + actionType +
                ", targetUser='" + targetUser + '\'' +
                ", lotNumber=" + lotNumber +
                ", from=" + from +
                ", to=" + to +
                ", user='" + user + '\'' +
                ", userAgent='" + userAgent + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
