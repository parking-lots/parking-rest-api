package parking.beans.request;

import org.hibernate.validator.constraints.Email;
import parking.utils.AccountStatus;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

public class EditUserForm {

    private String fullName;
    @Size(min = 6, max = 10)
    private String password;
    @Email
    private String email;
    private List<String> carRegNoList = new ArrayList<>();
    private AccountStatus status;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public List<String> getCarRegNoList() {
        return carRegNoList;
    }

    public void setCarRegNoList(List<String> carRegNoList) {
        this.carRegNoList = carRegNoList;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }
}
