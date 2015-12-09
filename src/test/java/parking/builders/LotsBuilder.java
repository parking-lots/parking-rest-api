package parking.builders;

import parking.beans.response.ParkingLot;

import java.util.Date;

public class LotsBuilder {

    private ParkingLot lot;

    public LotsBuilder() {
        this.lot = new ParkingLot();
        this.lot.setFreeFrom(null);
        this.lot.setFreeTill(null);
    }

    public LotsBuilder number(Integer number) {
        this.lot.setNumber(number);
        return this;
    }

    public LotsBuilder owner(String owner) {
        this.lot.setOwner(owner);
        return this;
    }

    public LotsBuilder freeFrom(Date from) {
        this.lot.setFreeFrom(from);
        return this;
    }

    public LotsBuilder freeTill(Date till) {
        this.lot.setFreeTill(till);
        return this;
    }

    public LotsBuilder currentUsed(String current) {
        this.lot.setCurrentlyUsed(current);
        return this;
    }

    public ParkingLot build() {
        return this.lot;
    }
}
