package parking.repositories;

import parking.beans.document.Account;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.SetUnusedRequest;
import parking.beans.document.ParkingLot;

import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields(Account user);
    public void freeOwnersParking(SetUnusedRequest request);
    public void recallParking(ParkingNumberRequest request);
    public void reserve(ParkingNumberRequest request, Account user);
    public void cancelReservation(Account user);
}