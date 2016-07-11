package parking.beans.request;

import org.springframework.format.annotation.DateTimeFormat;
import parking.utils.EliminateDateTimestamp;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class RecallParking {
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private List<Date> availableDates;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeFrom;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private Date freeTill;
    private EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();

    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();
    }

    public List<Date> getAvailableDates() {
        return availableDates;
    }

    public void setAvailableDates(List<Date> availableDates) {
        for (Date d : availableDates) {
            d = eliminateDateTimestamp.formatDateForDatabase(d).getTime();
        }
        this.availableDates = availableDates;
    }
}
