package parking.beans.document;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.annotation.DateTimeFormat;
import parking.beans.response.Response;

import javax.validation.constraints.Digits;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.Date;

@Document(collection = "lots")
public class ParkingLot extends Response {

    @Id
    private ObjectId id;
    @Digits(integer = 10, fraction = 0)
    @Min(1)
    private Integer number;
    @Max(1)
    @Min(-2)
    private Integer floor;
    private  Boolean current;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(iso = ISO.DATE)
    private Date freeFrom;
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "CET")
    @DateTimeFormat(iso = ISO.DATE)
    private Date freeTill;


    private Date reserved;
    @DBRef
    private Account user;
    @DBRef
    private Account owner;

    public ParkingLot() {

    }

    public ParkingLot(Integer number, Integer floor) {
        this.number = number;
        this.floor = floor;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
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
