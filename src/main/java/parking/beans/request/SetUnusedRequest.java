package parking.beans.request;

import org.joda.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.util.Calendar;
import java.util.Date;

public class SetUnusedRequest {
    private Integer number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom = new Date();
    //@NotNull(message = "Must provide untill which date are you giving your parking away!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill = new Date();
    //private Date freeTill;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        if (freeFrom == null){
            return;
        }
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        if (freeTill == null){
            return;
        }
        this.freeTill = freeTill;
    }
}
