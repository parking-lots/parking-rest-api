package loans.beans.request;

import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class setUnusedRequest {
    @NotNull(message = "Must provide parking number, stupid!")
    private Integer number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @NotNull(message = "Must provide untill which date are you giving your parking away!")
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
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        this.freeTill = freeTill;
    }
}