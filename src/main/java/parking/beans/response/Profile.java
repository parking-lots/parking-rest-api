package parking.beans.response;


import parking.beans.document.Account;
import parking.beans.document.ParkingLot;

public class Profile extends Response {

    private String fullName;
    private String username;
    private Integer parkingNumber;
    private Integer flor;
    private Profile owner;
    private Parking parking;

    public Profile(Account account) {
        fullName = account.getFullName();
        username = account.getUsername();
        parkingNumber = account.getParkingNumber();
        flor = account.getFlor();
    }

    public Profile(Account account, Boolean parkingLot) {
        fullName = account.getFullName();
        username = account.getUsername();
        parkingNumber = account.getParkingNumber();
        flor = account.getFlor();
        if (parkingLot) {
            parking = (account.getParking() !=null)? new Parking(account.getParking(), false): null;
        }
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

    public Parking getParking() {
        return parking;
    }

    public void setParking(Parking parking) {
        this.parking = parking;
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
