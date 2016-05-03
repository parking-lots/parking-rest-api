package parking.repositories;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.request.ParkingNumberRequest;
import parking.beans.request.RecallSingleParking;
import parking.beans.request.SetUnusedRequest;
import parking.utils.ParkingType;

import java.util.Date;
import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields(Account user);

    public void freeOwnersParking(Integer lotNumber, Date freeFrom, Date freeTill);

    public void recallParking(Integer lotNumber);

    public void recallSingleParking(Integer lotNumber, Date freeFrom, Date freeTill);

    public void reserve(Integer lotNumber, Account user);

    public void cancelReservation(Account user);

    public List<ParkingLot> findParking(ParkingType type);
}