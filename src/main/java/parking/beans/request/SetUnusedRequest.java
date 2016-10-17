package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;
import parking.utils.EliminateDateTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;

public class SetUnusedRequest {
    private LinkedList<LocalDate> availableDates;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate freeTill;


    public LinkedList<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(LinkedList<LocalDate> availableDates) {
        this.availableDates = availableDates;
    }

    public LocalDate getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(LocalDate freeFrom) {
        if (freeFrom == null) {
            freeFrom = LocalDate.now();
            this.freeFrom = freeFrom;
        } else {
            this.freeFrom = freeTill;
        }
    }

    public LocalDate getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(LocalDate freeTill) {
        if (freeTill == null) {
            freeTill = LocalDate.now();
            this.freeTill = freeTill;
        } else {
            this.freeTill = freeFrom;
        }
    }
}
