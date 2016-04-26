package parking.beans.request;

import parking.beans.document.Account;

import javax.validation.Valid;

/**
 * Created by Lina on 25/04/16.
 */
public class EditUserForm {
    @Valid
    private Account account;

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

}
