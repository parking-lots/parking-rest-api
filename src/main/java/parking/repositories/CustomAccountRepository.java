package parking.repositories;

import parking.beans.document.Account;

public interface CustomAccountRepository {
    void editAccount(Account account);
    void attachParking(Integer lotNumber, String username);
    void removeParking(String username);
}
