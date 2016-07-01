package parking.beans.response;

import parking.beans.document.Account;
import parking.beans.document.Role;

import java.util.ArrayList;
import java.util.List;

public class User extends Response {
    private String fullName;
    private String username;
    private String email;
    private String number;
    private List<String> carList = new ArrayList<>();
    private List<String> roles = new ArrayList<>();

    public User(Account account) {
        this.fullName = account.getFullName();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.number = (account.getParking() == null) ? "" : account.getParking().getNumber().toString();
        this.carList = account.getCarRegNoList();

        if (!account.getRoles().isEmpty()) {
            for (Role role : account.getRoles()) {
                this.roles.add(role.getName());
            }
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public List<String> getCarList() {
        return carList;
    }

    public void setCarList(List<String> carList) {
        this.carList = carList;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
