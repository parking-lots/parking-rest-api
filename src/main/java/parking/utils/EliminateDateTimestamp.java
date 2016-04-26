package parking.utils;

import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class EliminateDateTimestamp {
    public Calendar formatDateForDatabase(Date oldDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(oldDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }
}
