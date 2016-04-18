package parking.builders;

import parking.beans.document.Account;
import parking.beans.response.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lina on 15/04/16.
 */
public class UserBuilder{

    private User user;

    private String name;
    private String username;
    private List<String> roles = new ArrayList<>();
    private String number;
    Account account;

    public UserBuilder(){
        account = new Account();
        account.setUsername("nickname");
        account.setFullName("Nick Namesson");

        //Role role
        //account.setRoles("OWNER");
       // roles.add("ADMIN");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public User build() {
        User user = new User(account);
        user.setRoles(roles);

        return user;
    }
}
