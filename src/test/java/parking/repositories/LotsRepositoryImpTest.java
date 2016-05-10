package parking.repositories;

import org.junit.Test;
import org.mockito.InjectMocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class LotsRepositoryImpTest {

    @InjectMocks
    LotsRepositoryImpl lotsRepository;

    @Test
    public void recallParkingTest(){
        Date availableDate1 = new Date(1462924800000L); //2016-05-11
        Date availableDate2 = new Date(1463011200000L);//2016-05-12

        //assertEquals(1, lotsRepository.recallParking(200, availableDate1));
    }
}
