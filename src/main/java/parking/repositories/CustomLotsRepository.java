package parking.repositories;

import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.response.ParkingLot;

import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields(String userName);
    public void freeOwnersParking(SetUnusedRequest request);
    public void recallParking(ParkingNumberRequest request);
    public void reserve(ParkingNumberRequest request, String userName);
    public void cancelReservation(String currentUserName);
}