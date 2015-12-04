package parking.repositories;

import parking.beans.request.parkingNumberRequest;
import parking.beans.request.setUnusedRequest;
import parking.beans.response.ParkingLot;

import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields();
    public void freeOwnersParking(setUnusedRequest request);
    public void recallParking(parkingNumberRequest request);
    public void reserve(parkingNumberRequest request);
}