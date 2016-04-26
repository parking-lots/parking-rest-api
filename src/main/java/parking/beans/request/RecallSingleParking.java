package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;
import parking.utils.EliminateDateTimestamp;

import java.util.Date;

public class RecallSingleParking {
    private Integer number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill;
    private EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();

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
        this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();// formatDateForDatabase(freeFrom).getTime();
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) { this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();// formatDateForDatabase(freeTill).getTime();
    }
}
