package parking.repositories;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LotsRepositoryTest {


    @Mock
    private MongoOperations operations;

    private LotsRepositoryImpl lotsRepository;

    private LinkedList<LocalDate> newShareDates = new LinkedList<LocalDate>();
    private LinkedList<LocalDate> oldShareDates = new LinkedList<LocalDate>();
    private LinkedList<LocalDate> merged = new LinkedList<LocalDate>();
    private LinkedList<LocalDate> unshareDates = new LinkedList<LocalDate>();
    private List<ParkingLot> lots;

    private Integer lotNumber = 161;

    @Before
    public void initMock() {
        lotsRepository = new LotsRepositoryImpl(operations);

        newShareDates.add(LocalDate.now());
        newShareDates.add(LocalDate.now().plusDays(1));
        newShareDates.add(LocalDate.now().plusDays(2));
        newShareDates.add(LocalDate.now().plusDays(9));
        newShareDates.add(LocalDate.now().plusDays(8));
        newShareDates.add(LocalDate.now().plusDays(5));

        oldShareDates.add(LocalDate.now().minusDays(1));
        oldShareDates.add(LocalDate.now().plusDays(3));
        oldShareDates.add(LocalDate.now().plusDays(4));
        oldShareDates.add(LocalDate.now().plusDays(7));
        oldShareDates.add(LocalDate.now().plusDays(5));
        oldShareDates.add(LocalDate.now().plusDays(8));
        oldShareDates.add(LocalDate.now().plusDays(10));

        merged.add(LocalDate.now());
        merged.add(LocalDate.now().plusDays(1));
        merged.add(LocalDate.now().plusDays(2));
        merged.add(LocalDate.now().plusDays(3));
        merged.add(LocalDate.now().plusDays(4));
        merged.add(LocalDate.now().plusDays(5));
        merged.add(LocalDate.now().plusDays(7));
        merged.add(LocalDate.now().plusDays(8));
        merged.add(LocalDate.now().plusDays(9));
        merged.add(LocalDate.now().plusDays(10));

        unshareDates.add(LocalDate.now().plusDays(3));
        unshareDates.add(LocalDate.now().plusDays(4));

        Query searchQuery = new Query(Criteria.where("number").is(lotNumber));
        lots = new ArrayList<ParkingLot>();
        ParkingLot lot = new ParkingLot();
        lot.setDates(oldShareDates);
        lots.add(lot);
        given(operations.find(searchQuery, ParkingLot.class)).willReturn(lots);
        given(operations.findOne(searchQuery, ParkingLot.class)).willReturn(lots.get(0));
    }

    @Test
    public void shouldFetchDatesForParkingLot() throws ApplicationException {
        lotsRepository.shareParking(lotNumber, newShareDates);
        ArgumentCaptor<Query> capator = ArgumentCaptor.forClass(Query.class);
        verify(operations).find(capator.capture(), eq(ParkingLot.class));

        assertEquals(capator.getValue(), new Query(Criteria.where("number").is(lotNumber)));
    }

    @Test
    public void shouldFetchDatesForUnShare() throws ApplicationException {
        lotsRepository.unshareParking(lotNumber, unshareDates);
        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(operations).findOne(captor.capture(), eq(ParkingLot.class));

        assertEquals(captor.getValue(), new Query(Criteria.where("number").is(lotNumber)));
    }

    @Test
    public void shouldUpdateParkingWithoutUnShareDates() {
        lotsRepository.unshareParking(lotNumber, unshareDates);
        ArgumentCaptor<ParkingLot> update = ArgumentCaptor.forClass(ParkingLot.class);
        verify(operations).save(update.capture());

        oldShareDates.remove(0);
        oldShareDates.remove(0);
        oldShareDates.remove(0);

        Collections.sort(oldShareDates);

        assertEquals(update.getValue().getDates(), oldShareDates);
    }

    @Test
    public void shouldUpdateWithProperMergerdCollection() throws ApplicationException {
        lotsRepository.shareParking(lotNumber, newShareDates);
        ArgumentCaptor<ParkingLot> update = ArgumentCaptor.forClass(ParkingLot.class);
        verify(operations).save(update.capture());

        assertEquals(update.getValue().getDates(), merged);
    }

}
