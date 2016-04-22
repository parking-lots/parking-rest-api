package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class RecallSingleParking {
    private Integer number;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom = new Date();
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
        if (freeFrom == null){
            return;
        }
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        this.freeTill = freeTill;
    }
}
