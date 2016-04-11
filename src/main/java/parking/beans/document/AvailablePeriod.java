package parking.beans.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created by Lina on 07/04/16.
 */
public class AvailablePeriod {
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CET")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill;

    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date freeFrom) {
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill( @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Date freeTill) {
        this.freeTill = freeTill;
    }
}
