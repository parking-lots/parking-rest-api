package parking.beans.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.helper.AvailableDatesConverter;

import javax.validation.constraints.Digits;
import java.time.LocalDate;
import java.util.*;

public class Parking extends Response {
    private Integer number;
    private Profile owner;
    private Integer floor;
    private Profile user;

    public Parking(ParkingLot lot, Boolean owner) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
        this.owner = (lot.getOwner() != null) ? new Profile(lot.getOwner(), false) : null;
        this.user = (Optional.ofNullable(lot.getUser())).isPresent() ? new Profile(lot.getUser(), false): null;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Profile getOwner() {
        return owner;
    }

    public void setOwner(Profile owner) {
        this.owner = owner;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}
