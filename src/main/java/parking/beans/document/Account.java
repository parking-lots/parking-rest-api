package parking.beans.document;

import org.omg.PortableInterceptor.INACTIVE;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import parking.beans.response.ParkingLot;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class Account {

    @Id
    private String id;
    private String fullName;
    private String username;
    private String password;
    private Integer parkingNumber;
    private Integer flor;
    @DBRef
    private List<Role> roles = new ArrayList<Role>();
    @DBRef
    private ParkingLot parking;

    public Account(){}

    public Account(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public List<Role> getRoles() {
        return roles;
    }
    public void setRoles(List<Role> roles) {
        if (roles != null) {
            this.roles = roles;
        }
    }
    public ParkingLot getParking() {
        return parking;
    }
    public void setParking(ParkingLot parking) {
        this.parking = parking;
    }
}