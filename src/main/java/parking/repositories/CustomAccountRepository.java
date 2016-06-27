package parking.repositories;

import parking.beans.request.EditUserForm;

public interface CustomAccountRepository {
    void editAccount(EditUserForm newAccount, String username);
    void attachParking(Integer lotNumber, String username);
    void detachParking(String username);
}
