package loans.beans.response;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Document(collection = "lots")
public class ParkingLot extends Response {

    private Integer number;
    private String owner;
    private String floor;
    private  Boolean current;
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date freeFrom;
    @DateTimeFormat(iso = ISO.DATE_TIME)
    private Date freeTill;
    private Boolean currentlyUsed;

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

    public void setFreeFrom(@DateTimeFormat(iso = ISO.DATE_TIME) Date freeFrom) {
        this.freeFrom = freeFrom;
    }

    public Date getFreeTill() {
        return freeTill;
    }

    public void setFreeTill( @DateTimeFormat(iso = ISO.DATE_TIME) Date freeTill) {
        this.freeTill = freeTill;
    }

    public Boolean getCurrentlyUsed() {
        return currentlyUsed;
    }

    public void setCurrentlyUsed(Boolean currentlyUsed) {
        this.currentlyUsed = currentlyUsed;
    }
}
