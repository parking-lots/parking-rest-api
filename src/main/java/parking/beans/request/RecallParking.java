package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;
import parking.utils.EliminateDateTimestamp;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RecallParking {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private List<LocalDate> availableDates;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate freeTill;

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

    public List<LocalDate> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<LocalDate> availableDates) {
        this.availableDates = availableDates;
    }
}
