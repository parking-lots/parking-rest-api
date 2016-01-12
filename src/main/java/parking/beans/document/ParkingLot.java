package parking.beans.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.DateTimeFormat;
import parking.beans.document.Account;
import parking.beans.response.Response;

import java.util.Date;

@Document(collection = "lots")
public class ParkingLot extends Response {

    private Integer number;
    private String floor;
    private  Boolean current;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = ISO.DATE)
    private Date freeFrom;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = ISO.DATE)
    private Date freeTill;
    @JsonFormat(pattern = "yyy-MM-dd")
    @DateTimeFormat(iso = ISO.DATE)
    private Date reserved;
    @DBRef
    private Account user;
    @DBRef
    private Account owner;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Account getOwner() {
        return owner;
    }

    public void setOwner(Account owner) {
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

    public Account getUser() {
        return user;
    }

    public void setUser(Account user) {
        this.user = user;
    }

    public Date getReserved() {
        return reserved;
    }

    public void setReserved(Date reserved) {
        this.reserved = reserved;
    }
}
