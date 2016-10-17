package parking.helper;

import parking.beans.document.AvailablePeriod;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class AvailableDatesConverter {
    public static List<AvailablePeriod> convertToInterval(LinkedList<LocalDate> availableDates) {
        AvailablePeriod availablePeriod = null;
        ArrayList<AvailablePeriod> availablePeriods = new ArrayList<>();


        if (!Optional.ofNullable(availableDates).isPresent()) {
            return null;
        }

        Collections.sort(availableDates);

        for (LocalDate date : availableDates) {
            if (availablePeriod == null) {
                availablePeriod = new AvailablePeriod(date, date);
                continue;
            }

            if (Period.between(availablePeriod.getFreeTill(), date).getDays() == 1) {
                availablePeriod.setFreeTill(date);
            } else {
                availablePeriods.add(availablePeriod);
                availablePeriod = new AvailablePeriod(date, date);
            }
        }
        if (Optional.ofNullable(availableDates).isPresent()) {
            availablePeriods.add(availablePeriod);
        }

        return availablePeriods;
    }
}
