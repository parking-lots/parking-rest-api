package parking.beans.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import parking.beans.document.ParkingLot;
import java.util.Date;

public class Parking extends Response {
    private Integer number;
    private Profile owner;
    private Integer floor;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeFrom;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date freeTill;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Europe/Vilnius")
    private Date reserved;
    private Profile user;

    public Parking(ParkingLot lot, Boolean owner) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
        this.freeFrom = lot.getFreeFrom();
        this.freeTill = lot.getFreeTill();
        this.reserved = lot.getReserved();
        this.user = (lot.getUser() != null)? new Profile(lot.getUser(), false): null;
        if (owner) {
            this.owner = (lot.getOwner() != null)? new Profile(lot.getOwner(), false): null;
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


    public Date getFreeFrom() {
        return freeFrom;
    }

    public void setFreeFrom(Date freeFrom) {
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill(Date freeTill) {
        this.freeTill = freeTill;
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
