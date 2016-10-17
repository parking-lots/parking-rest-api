package parking.beans.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.helper.AvailableDatesConverter;

import java.time.LocalDate;
import java.util.Date;
import java.util.LinkedList;

public class Parking extends Response {
    private Integer number;
    private Profile owner;
    private Integer floor;
    private LinkedList<AvailablePeriod> availablePeriods = new LinkedList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date reserved;
    private Profile user;

    public Parking(ParkingLot lot, Boolean owner) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
        this.availablePeriods = AvailableDatesConverter.convertToInterval(lot.getDates());
        this.reserved = lot.getReserved();
        this.user = (lot.getUser() != null) ? new Profile(lot.getUser(), false) : null;
        if (owner) {
            this.owner = (lot.getOwner() != null) ? new Profile(lot.getOwner(), false) : null;
        }
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

    public LinkedList<AvailablePeriod> getAvailablePeriods() {
        return availablePeriods;
    }

    public void setAvailablePeriods(LinkedList<AvailablePeriod> availablePeriods) {
        this.availablePeriods = availablePeriods;
    }

    public Date getReserved() {
        return reserved;
    }

    public void setReserved(Date reserved) {
        this.reserved = reserved;
    }

    public Profile getUser() {
        return user;
    }

    public void setUser(Profile user) {
        this.user = user;
    }
}
