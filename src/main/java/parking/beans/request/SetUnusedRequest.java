package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;
import parking.utils.EliminateDateTimestamp;

import java.util.Date;
import java.util.LinkedList;

public class SetUnusedRequest {
    //private Integer number;
    //@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LinkedList<Date> availableDates;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill;
    private EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();

//    public Integer getNumber() {
//        return number;
//    }
//
//    public void setNumber(Integer number) {
//        this.number = number;
//    }


    public LinkedList<Date> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(LinkedList<Date> availableDates) {
        this.availableDates = availableDates;
    }

    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        if (freeFrom == null) {
            freeFrom = new Date();
            this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();// formatDateForDatabase(freeFrom).getTime();
        } else {
            this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();// formatDateForDatabase(freeFrom).getTime();
        }
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        if (freeTill == null) {
            freeTill = new Date();
            this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();// formatDateForDatabase(freeTill).getTime();
        } else {
            this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();// formatDateForDatabase(freeTill).getTime();
        }
    }
}
