package parking.beans.response;


import parking.repositories.Account;

import java.util.Optional;

public class Profile extends Response {

    private String fullName;
    private String username;
    private Integer parkingNumber;
    private Integer flor;

    public Profile(Account account) {
        fullName = account.getFullName();
        username = account.getUsername();
        parkingNumber = account.getParkingNumber();
        flor = account.getFlor();
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

    public Integer getParkingNumber() {
        return parkingNumber;
    }

    public void setParkingNumber(Integer parkingNumber) {
        this.parkingNumber = parkingNumber;
    }

    public Integer getFlor() {
        return flor;
    }

    public void setFlor(Integer flor) {
        this.flor = flor;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", parkingNumber=" + parkingNumber +
                ", flor=" + flor +
                '}';
    }
}
