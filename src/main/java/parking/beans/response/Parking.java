package parking.beans.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class Parking extends Response {
    private Integer number;
    private String owner;
    private String floor;
    private  Boolean current;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date freeFrom;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date freeTill;
    @JsonFormat(pattern = "yyy-MM-dd")
    private Date reserved;
    private Profile user;

    public Parking(ParkingLot lot) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
        this.freeFrom = lot.getFreeFrom();
        this.freeTill = lot.getFreeTill();
        this.reserved = lot.getReserved();
        this.user = (lot.getUser() != null)? new Profile(lot.getUser()): null;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getFloor() {
        return floor;
    }

    public void setFloor(String floor) {
        this.floor = floor;
    }

    public Boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
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
