package parking.builders;

import parking.beans.document.Account;
import parking.beans.document.AvailablePeriod;
import parking.beans.document.ParkingLot;

import java.util.Date;
import java.util.LinkedList;

public class LotsBuilder {

    private ParkingLot lot;

    public LotsBuilder() {
        LinkedList<AvailablePeriod> availablePeriods = new LinkedList<>();
        AvailablePeriod availablePeriod = new AvailablePeriod();
        availablePeriod.setFreeFrom(null);
        availablePeriod.setFreeTill(null);
        this.lot = new ParkingLot();
        this.lot.setAvailablePeriods(availablePeriods);
    }

    public LotsBuilder number(Integer number) {
        this.lot.setNumber(number);
        return this;
    }

    public LotsBuilder owner(Account owner) {
        this.lot.setOwner(owner);
        return this;
    }

    /*public LotsBuilder freeFrom(Date from) {
        this.lot.setFreeFrom(from);
        return this;
    } */

    /*public LotsBuilder freeTill(Date till) {
        this.lot.setFreeTill(till);
        return this;
    }*/

    public LotsBuilder user(Account current) {
        this.lot.setUser(current);
        return this;
    }

    public ParkingLot build() {
        return this.lot;
    }
}
