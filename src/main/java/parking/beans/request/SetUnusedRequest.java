package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Calendar;
import java.util.Date;

public class SetUnusedRequest {
    private Integer number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill;

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
        if (freeFrom == null) {
            freeFrom = new Date();
            this.freeFrom = formatDateForDatabase(freeFrom).getTime();
        } else {
            this.freeFrom = formatDateForDatabase(freeFrom).getTime();
        }
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        if (freeTill == null) {
            freeTill = new Date();
            this.freeTill = formatDateForDatabase(freeTill).getTime();
        } else {
            this.freeTill = formatDateForDatabase(freeTill).getTime();
        }
    }

    private Calendar formatDateForDatabase(Date oldDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(oldDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
