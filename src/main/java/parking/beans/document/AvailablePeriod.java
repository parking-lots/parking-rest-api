package parking.beans.document;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public class AvailablePeriod {
    private LocalDate freeFrom;
    private LocalDate freeTill;

    public AvailablePeriod(LocalDate freeFrom, LocalDate freeTill) {
        this.freeFrom = freeFrom;
        this.freeTill = freeTill;
    }

    public LocalDate getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(LocalDate freeFrom) {
        this.freeFrom = freeFrom;
    }

    public LocalDate getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(LocalDate freeTill) {
        this.freeTill = freeTill;
    }
}
