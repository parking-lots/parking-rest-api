package parking.beans.response;

import parking.beans.document.Account;
import parking.beans.document.ParkingLot;
import parking.beans.document.Role;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lina on 14/04/16.
 */
public class User extends Response {
    private String fullName;
    private String username;
    private String roleName;
    private List<String> Roles = new ArrayList<>();
    private String number;
    private ParkingLot parkingLot;

    public User(Account account){
        this.fullName = account.getFullName();
        this.username = account.getUsername();
        if (account.getRoles().size() != 0) {
            for (Role r : account.getRoles()) {
                this.roleName = r.getName();
                this.Roles.add(this.roleName);
            }
        }

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<String> getRoles() {
        return Roles;
    }

    public void setRoles(List<String> hasRoles) {
        this.Roles = hasRoles;
    }
}
