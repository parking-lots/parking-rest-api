package parking.beans.response;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;

/**
 * Created by Lina on 14/04/16.
 */
public class User extends Response {
    private String fullName;
    private String username;
    private String role;
    private String number;
    private ParkingLot parkingLot;

    public User(Account account){
        this.fullName = account.getFullName();
        this.username = account.getUsername();
        this.role = "owner";

        parkingLot = new ParkingLot();
        parkingLot = account.getParking();
        this.number = (parkingLot==null) ? "" : (parkingLot.getNumber().toString());

    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
