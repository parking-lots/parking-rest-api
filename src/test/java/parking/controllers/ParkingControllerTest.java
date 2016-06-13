package parking.controllers;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import parking.beans.document.ParkingLot;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.Parking;
import parking.builders.LotsBuilder;
import parking.exceptions.ApplicationException;
import parking.helper.ExceptionHandler;
import parking.helper.ExceptionMessage;
import parking.service.ParkingService;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class ParkingControllerTest {


    @InjectMocks
    private ParkingController controller;

    @Mock
    private ParkingService service;
    @Mock
    private HttpServletRequest httpRequest;
    @Mock
    SetUnusedRequest setUnusedRequest;
    @Mock
    ExceptionHandler exceptionHandler;
    @Mock
    ParkingService parkingService;
    @Mock
    ParkingLot mockedParkingLot;

    private List<ParkingLot> mockedParkingLotList = new ArrayList<ParkingLot>();
    private List<Parking> mockedParkingList = new ArrayList<Parking>();

    @Before
    public void initMockData() {
        mockedParkingLotList.add(new LotsBuilder().number(100).build());
        mockedParkingLotList.add(new LotsBuilder().number(101).build());
        mockedParkingLotList.add(new LotsBuilder().number(103).build());
        mockedParkingLotList.add(new LotsBuilder().number(104).build());

        Function<ParkingLot, Parking> mapper = lot -> new Parking(lot, true);
        mockedParkingList = mockedParkingLotList.stream()
                .map(mapper)
                .collect(Collectors.<Parking>toList());

        when(exceptionHandler.handleException(ExceptionMessage.PARKING_DOES_NOT_EXIST, httpRequest)).thenReturn(new ApplicationException("message"));
    }

    @Test
    public void whenAvailableShouldReturnList() throws ApplicationException {
        given(service.getAvailable(httpRequest)).willReturn(mockedParkingLotList);

        assertThat(controller.getAllAvailable(httpRequest), instanceOf(List.class));
    }

    @Test
    public void whenFreeOwnersParkingShouldCallService() throws ApplicationException {
        when(parkingService.getParkingNumberByUser()).thenReturn(mockedParkingLot);
        controller.freeOwnersParking(setUnusedRequest, httpRequest);
        parkingService.freeOwnersParking(any(ObjectId.class), any(Integer.class), any(Date.class), any(Date.class), eq(httpRequest));
    }

    @Test
    public void whenRecallParkingShoulCallService() throws ApplicationException {
        List<Date> dateList = new ArrayList<>();
        dateList.add(new Date());
        parkingService.recallParking(dateList, httpRequest);
        verify(parkingService, times(1)).recallParking(dateList, httpRequest);
    }

    @Test
    public void whenReserveShouldCallService() throws ApplicationException {
        parkingService.reserve(mockedParkingLot.getNumber(), httpRequest);
        verify(parkingService, times(1)).reserve(mockedParkingLot.getNumber(), httpRequest);
    }

    @Test
    public void whenCancelReservationShouldRecallParking() throws ApplicationException {
        parkingService.cancelReservation(httpRequest);
        verify(parkingService, times(1)).cancelReservation(httpRequest);
    }
}