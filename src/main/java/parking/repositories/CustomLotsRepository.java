package parking.repositories;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.exceptions.ApplicationException;
import parking.utils.ParkingType;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CustomLotsRepository {
    public List<ParkingLot> searchAllFields(Account user);

    public void shareParking(Integer lotNumber, List<LocalDate> dates) throws  ApplicationException;

    public void unshareParking(Integer lotNumer, List<LocalDate> unshareDates);

    public void reserve(Integer lotNumber, Account user, HttpServletRequest httpRequest) throws ApplicationException;

    public void cancelReservation(Account user);

    public List<ParkingLot> findParking(ParkingType type);

    public void setParkingOwner(Integer lotNumber, String username);

    public void removeParkingOwner(Integer lotNumber);
}