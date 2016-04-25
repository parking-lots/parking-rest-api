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
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill = new Date();

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
