package parking.builders;

/**
 * Created by Lina on 15/04/16.
 */
public class UserBuilder {
    private String name;
    private String username;
    private String role;
    private String number;

    public UserBuilder(String name, String username, String role, String number){
        this.name = name;
        this.username = username;
        this.role = role;
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
