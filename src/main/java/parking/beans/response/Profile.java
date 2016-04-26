package parking.beans.response;


import parking.beans.document.Account;

public class Profile extends Response {

    private String fullName;
    private String username;
    private Profile owner;
    private Parking parking;

    public Profile(Account account) {
        fullName = account.getFullName();
        username = account.getUsername();

    }

    public Profile(Account account, Boolean parkingLot) {
        fullName = account.getFullName();
        username = account.getUsername();
        if (parkingLot) {
            parking = (account.getParking() != null) ? new Parking(account.getParking(), false) : null;
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
                '}';
    }
}
