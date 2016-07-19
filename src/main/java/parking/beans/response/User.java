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
    private List<String> carRegNoList = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
    private boolean active;
    private boolean emailConfirmed;

    public User(Account account) {
        this.fullName = account.getFullName();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.number = (account.getParking() == null) ? "" : account.getParking().getNumber().toString();
        this.carRegNoList = account.getCarRegNoList();

        if (!account.getRoles().isEmpty()) {
            for (Role role : account.getRoles()) {
                this.roles.add(role.getName());
            }
        }

        this.active = account.isActive();
        this.emailConfirmed = account.isEmailConfirmed();
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

    public List<String> getCarRegNoList() {
        return carRegNoList;
    }

    public void setCarRegNoList(List<String> carRegNoList) {
        this.carRegNoList = carRegNoList;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isEmailConfirmed() {
        return emailConfirmed;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        this.emailConfirmed = emailConfirmed;
    }

    @Override
    public String toString() {
        return "User{" +
                "fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", number='" + number + '\'' +
                ", carRegNoList=" + carRegNoList +
                ", roles=" + roles +
                ", active=" + active +
                ", emailConfirmed=" + emailConfirmed +
                '}';
    }
}
