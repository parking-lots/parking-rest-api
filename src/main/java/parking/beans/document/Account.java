package parking.beans.document;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import parking.helper.ProfileHelper;
import parking.helper.ToolHelper;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "users")
public class Account {

    @Id
    private ObjectId id;
    @NotNull
    private String fullName;
    @NotNull
    private String username;
    @Size(min = 6, max = 10)
    private String password;
    private String email;
    private List<Car> carList = new ArrayList<>();
    @DBRef
    private List<Role> roles = new ArrayList<Role>();
    @DBRef
    private ParkingLot parking;

    public Account(){}

    public Account(String fullName, String username, String password) {
        this.fullName = fullName;
        this.username = username;
        this.password = ProfileHelper.encryptPassword(password);
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
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

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public ParkingLot getParking() {
        return parking;
    }
    public void setParking(ParkingLot parking) {
        this.parking = parking;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Car> getCarList() {
        return carList;
    }

    public void setCarList(List<Car> regNoList) {
        this.carList = regNoList;
    }
}
