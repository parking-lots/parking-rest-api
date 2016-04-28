package parking.builders;

import parking.beans.document.Account;
import parking.beans.document.Car;
import parking.beans.response.User;
import java.util.ArrayList;
import java.util.List;
import parking.beans.document.Role;

public class AccountBuilder{

    private User user;

    private String fullName;
    private String username;
    private String password;
    private String email;
    private List<Car> carList = new ArrayList<>();
    private List<Role> roles = new ArrayList<>();

    Account account;

    public AccountBuilder(){
        account = new Account();
        account.setUsername("nickname");
        account.setFullName("Nick Namesson");
        account.setEmail("nick.namesson@swed.lt");
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setCarList(List<Car> carList) {
        this.carList = carList;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public Account build() {
        Account account = new Account();

        return account;
    }
}
