package parking.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.response.ParkingLot;
import parking.builders.LotsBuilder;
import parking.service.ParkingService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ParkingControllerTest {


    @InjectMocks
    private ParkingController controller;

    @Mock
    private ParkingService service;

    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();

    @Before
    public void initMockData() {
        mockedParkingLotList.add(new LotsBuilder().number(100).owner("Name Surname").build());
        mockedParkingLotList.add(new LotsBuilder().number(101).owner("Name Surname2").build());
        mockedParkingLotList.add(new LotsBuilder().number(103).owner("Name Surname3").build());
        mockedParkingLotList.add(new LotsBuilder().number(104).owner("Name Surname4").build());
    }

    @Test
    public void whenAvailableShouldReturnAvailableItems() {
        given(service.getAvailable()).willReturn(mockedParkingLotList);

        assert(controller.getAllAvailable()).containsAll(mockedParkingLotList);
    }
}