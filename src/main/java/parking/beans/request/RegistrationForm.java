package parking.beans.request;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;

import javax.validation.Valid;

public class RegistrationForm {

    @Valid
    private Account account;
    @Valid
    private ParkingLot parking;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ParkingLot getParking() {
        return parking;
    }

    public void setParking(ParkingLot parking) {
        this.parking = parking;
    }
}
