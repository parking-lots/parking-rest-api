package parking.beans.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class AvailablePeriods extends Response {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeTill;

    public AvailablePeriods(Date freeFrom, Date freeTill) {
        this.freeFrom = freeFrom;
        this.freeTill = freeTill;
    }

    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        this.freeTill = freeTill;
    }
}
