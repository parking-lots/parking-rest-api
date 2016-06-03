package parking.beans.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import parking.utils.EliminateDateTimestamp;

import java.util.Date;

/**
 * Created by Lina on 07/04/16.
 */
public class AvailablePeriod {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeTill;

    public AvailablePeriod(Date freeFrom, Date freeTill){
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();
        this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();
    }

    public Date getFreeFrom() {

        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        this.freeFrom = eliminateDateTimestamp.formatDateForDatabase(freeFrom).getTime();
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        EliminateDateTimestamp eliminateDateTimestamp = new EliminateDateTimestamp();
        this.freeTill = eliminateDateTimestamp.formatDateForDatabase(freeTill).getTime();
    }
}
