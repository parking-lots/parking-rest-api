package parking.beans.request;

/**
 * Created by Lina on 28/04/16.
 */
public class AttachParking {
    Integer lotNumber;
    String username;

    public Integer getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(Integer lotNumber) {
        this.lotNumber = lotNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
