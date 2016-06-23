package parking.beans.response;

import parking.beans.document.ParkingLot;

/**
 * @author Julius
 */
public class FreeParkingLot extends Response {

    private Integer number;
    private Integer floor;

    public FreeParkingLot(ParkingLot lot) {
        this.number = lot.getNumber();
        this.floor = lot.getFloor();
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getFloor() {
        return floor;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

}
