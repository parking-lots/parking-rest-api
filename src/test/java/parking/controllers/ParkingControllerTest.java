package parking.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.response.Parking;
import parking.beans.document.ParkingLot;
import parking.builders.LotsBuilder;
import parking.exceptions.UserException;
import parking.service.ParkingService;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParkingControllerTest {


    @InjectMocks
    private ParkingController controller;

    @Mock
    private ParkingService service;

    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private List<Parking> mockedParkingList = new ArrayList<Parking>();

    @Before
    public void initMockData() {
        mockedParkingLotList.add(new LotsBuilder().number(100).build());
        mockedParkingLotList.add(new LotsBuilder().number(101).build());
        mockedParkingLotList.add(new LotsBuilder().number(103).build());
        mockedParkingLotList.add(new LotsBuilder().number(104).build());

        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        mockedParkingList =  mockedParkingLotList.stream()
                .map(mapper)
                .collect(Collectors.<Parking> toList());
    }

    @Test
    public void whenAvailableShouldReturnAvailableItems() throws UserException {
        given(service.getAvailable()).willReturn(mockedParkingLotList);

        assertThat(controller.getAllAvailable().get(0), instanceOf(Parking.class));
    }
}