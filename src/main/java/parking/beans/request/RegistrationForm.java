package parking.beans.request;

import parking.beans.document.Account;

import javax.validation.Valid;

public class RegistrationForm {

    @Valid
    private Account account;
    private Integer number;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
