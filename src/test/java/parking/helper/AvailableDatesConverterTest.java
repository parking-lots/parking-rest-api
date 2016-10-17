package parking.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.AvailablePeriod;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AvailableDatesConverterTest {

    @InjectMocks
    private AvailableDatesConverter availableDatesConverter;

    @Test
    public void checkConversionWithGaps() {
        List<AvailablePeriod> availablePeriods = new ArrayList<>();
        LocalDate freeFrom = LocalDate.of(2016, 11, 06);
        LocalDate freeTill = LocalDate.of(2016, 11, 11);

        availablePeriods.add(new AvailablePeriod(freeFrom, freeFrom));
        availablePeriods.add(new AvailablePeriod(freeTill, freeTill));

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(freeFrom);
        dateList.add(freeTill);

        assertEquals(2, (availableDatesConverter.convertToInterval((LinkedList<LocalDate>)dateList)).size());
        assertEquals(availablePeriods.get(0).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeFrom());
        assertEquals(availablePeriods.get(0).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeTill());

        assertEquals(availablePeriods.get(1).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(1).getFreeFrom());
        assertEquals(availablePeriods.get(1).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(1).getFreeTill());
    }

    @Test
    public void checkConversionWithoutGaps() {
        List<AvailablePeriod> availablePeriods = new ArrayList<>();
        LocalDate freeFrom = LocalDate.of(2016, 11, 11);
        LocalDate freeTill = LocalDate.of(2016, 11, 12);

        AvailablePeriod availablePeriod = new AvailablePeriod(freeFrom, freeTill);
        availablePeriods.add(availablePeriod);

        LinkedList<LocalDate> dateList = new LinkedList<>();
        dateList.add(freeFrom);
        dateList.add(freeTill);

        assertEquals(1, (availableDatesConverter.convertToInterval((LinkedList<LocalDate>)dateList)).size());
        assertEquals(availablePeriods.get(0).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeFrom());
        assertEquals(availablePeriods.get(0).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeTill());
    }
}
