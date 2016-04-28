package parking.repositories;

import parking.beans.document.Account;

public interface CustomAccountRepository {
    public void editAccount(Account request);
    public void attachParking(Integer lotNumber, String username);
}
