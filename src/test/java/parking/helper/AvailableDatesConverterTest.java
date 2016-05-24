package parking.helper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.AvailablePeriod;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class AvailableDatesConverterTest {

    @InjectMocks
    private AvailableDatesConverter availableDatesConverter;

    @Test
    public void checkConversionWithGaps(){
        List<AvailablePeriod> availablePeriods = new ArrayList<>();
        Date freeFrom = new Date(1478390400000L);//2016-11-06
        Date freeTill = new Date(1478822400000L);//2016-11-11

        AvailablePeriod availablePeriod = new AvailablePeriod(freeFrom, freeFrom);
        availablePeriods.add(availablePeriod);
        availablePeriod = new AvailablePeriod(freeTill, freeTill);
        availablePeriods.add(availablePeriod);

        List<Date> dateList = new ArrayList<>();
        dateList.add(freeFrom);
        dateList.add(freeTill);

        assertEquals(2, (availableDatesConverter.convertToInterval(dateList)).size());
        assertEquals(availablePeriods.get(0).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeFrom());
        assertEquals(availablePeriods.get(0).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeTill());

        assertEquals(availablePeriods.get(1).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(1).getFreeFrom());
        assertEquals(availablePeriods.get(1).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(1).getFreeTill());
    }

    @Test
    public void checkConversionWithoutGaps(){
        List<AvailablePeriod> availablePeriods = new ArrayList<>();
        Date freeFrom = new Date(1478822400000L);//2016-11-11
        Date freeTill = new Date(1478908800000L);//2016-11-12
        AvailablePeriod availablePeriod = new AvailablePeriod(freeFrom, freeTill);
        availablePeriods.add(availablePeriod);

        List<Date> dateList = new ArrayList<>();
        dateList.add(freeFrom);
        dateList.add(freeTill);

        assertEquals(1, (availableDatesConverter.convertToInterval(dateList)).size());
        assertEquals(availablePeriods.get(0).getFreeFrom(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeFrom());
        assertEquals(availablePeriods.get(0).getFreeTill(), availableDatesConverter.convertToInterval(dateList).get(0).getFreeTill());
    }

    @Test
    public void checkConversionWhenContainsNull(){

        List<Date> dateList = new ArrayList<>();

        Date date1 = new Date(1478908800000L);//2016-11-12
        Date date2 = null;
        Date date3 = new Date();

        dateList.add(date1);
        dateList.add(date2);
        dateList.add(date3);

        assertEquals(1, (availableDatesConverter.convertToInterval(dateList)).size());
    }
}
