package loans.repositories;

import loans.beans.request.parkingNumberRequest;
import loans.beans.request.setUnusedRequest;
import loans.beans.response.ParkingLot;

import java.util.List;

public interface CustomLostRepository {
    public List<ParkingLot> searchAllFields();
    public void freeOwnersParking(setUnusedRequest request);
    public void recallParking(parkingNumberRequest request);
}