package parking.beans.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class DeleteUser {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
