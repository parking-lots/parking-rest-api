package parking.beans.request;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ChangePassword {

    @NotNull(message = "Password is required!")
    @Size(min = 6, max = 10)
    private String newPassword;

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
