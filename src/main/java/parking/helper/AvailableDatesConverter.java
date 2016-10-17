package parking.helper;

import parking.beans.document.AvailablePeriod;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

public class AvailableDatesConverter {
    public static LinkedList<AvailablePeriod> convertToInterval(LinkedList<LocalDate> availableDates) {
        AvailablePeriod availablePeriod = null;
        LinkedList<AvailablePeriod> availablePeriods = new LinkedList<>();

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
        availablePeriods.add(availablePeriod);

        return availablePeriods;
    }
}
