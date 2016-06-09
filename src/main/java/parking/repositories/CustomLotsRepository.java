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

    public void freeOwnersParking(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException;

    public void checkPeriod(Integer lotNumber, Date freeFrom, Date freeTill, HttpServletRequest httpRequest) throws ApplicationException;

    public void recallParking(Integer lotNumber, Date freeFrom, Date freeTill);

    public void recallParking(Integer lotNumber, Date availableDate, HttpServletRequest httpRequest) throws ApplicationException;

    public void checkRecallDate(Integer lotNumber, Date availableDate, HttpServletRequest httpRequest) throws ApplicationException;

    public void reserve(Integer lotNumber, Account user, HttpServletRequest httpRequest) throws ApplicationException;

    public void cancelReservation(Account user);

    public List<ParkingLot> findParking(ParkingType type);
}