package parking.helper;

import parking.beans.document.AvailablePeriod;

import java.util.*;

public class AvailableDatesConverter {
    public static List<AvailablePeriod> convertToInterval(List<Date> availableDates) {
        AvailablePeriod availablePeriod = null;
        List<AvailablePeriod> availablePeriods = new ArrayList<>();

        if (availableDates.contains(null)) {
            availablePeriod = new AvailablePeriod(new Date(), new Date());
            availablePeriods.add(availablePeriod);
            return availablePeriods;
        }

        Collections.sort(availableDates);

        for (Date d : availableDates) {
            if (availablePeriod == null) {
                availablePeriod = new AvailablePeriod(d, d);
                continue;
            }
            if ((int) ((d.getTime() - availablePeriod.getFreeTill().getTime()) / (1000 * 60 * 60 * 24)) == 1) {
                availablePeriod.setFreeTill(d);
            } else {
                availablePeriods.add(availablePeriod);
                availablePeriod = new AvailablePeriod(d, d);
            }
        }
        availablePeriods.add(availablePeriod);

        return availablePeriods;
    }
}
