package parking.beans.response;


import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;
import parking.helper.AvailableDatesConverter;

import java.util.List;
import java.util.Optional;

public class ParkingBase extends Response {
    private Integer number;
    private Profile owner;
    private Integer floor;
    private Profile user;
    private List<AvailablePeriod> availablePeriods;

    public ParkingBase(ParkingLot lot) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
        this.user = (Optional.ofNullable(lot.getUser())).isPresent() ? new Profile(lot.getUser(), false): null;
        this.availablePeriods = AvailableDatesConverter.convertToInterval(lot.getDates());
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

    public List<AvailablePeriod> getAvailablePeriods() {
        return availablePeriods;
    }

    public void setAvailablePeriods(List<AvailablePeriod> availablePeriods) {
        this.availablePeriods = availablePeriods;
    }
}
