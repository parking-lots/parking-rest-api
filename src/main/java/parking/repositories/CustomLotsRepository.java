package parking.repositories;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallSingleParking;
import parking.beans.request.SetUnusedRequest;

import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields(Account user);

    public void freeOwnersParking(SetUnusedRequest request);

    public void recallParking(ParkingNumberRequest request);

    public void recallSingleParking(RecallSingleParking recallSingleParking);

    public void reserve(ParkingNumberRequest request, Account user);

    public void cancelReservation(Account user);
}