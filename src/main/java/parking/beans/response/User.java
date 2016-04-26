package parking.beans.response;

import parking.beans.document.Account;
import parking.beans.document.Role;

import java.util.ArrayList;
import java.util.List;

public class User extends Response {
    private String fullName;
    private String username;
    private List<String> roles = new ArrayList<>();
    private String number;

    public User(Account account) {
        this.fullName = account.getFullName();
        this.username = account.getUsername();

        if (!account.getRoles().isEmpty()) {
            for (Role role : account.getRoles()) {
                this.roles.add(role.getName());
            }
        }

        this.number = (account.getParking() == null) ? "" : account.getParking().getNumber().toString();
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
        return roles;
    }

    public void setRoles(List<String> hasRoles) {
        this.roles = hasRoles;
    }
}
