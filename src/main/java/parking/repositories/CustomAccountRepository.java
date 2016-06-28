package parking.repositories;

import parking.beans.request.EditUserForm;
import parking.exceptions.ApplicationException;

import javax.servlet.http.HttpServletRequest;

public interface CustomAccountRepository {
    void editAccount(EditUserForm newAccount, String username);
    void attachParking(Integer lotNumber, String username, HttpServletRequest httpRequest) throws ApplicationException;
    void detachParking(String username, HttpServletRequest httpRequest) throws ApplicationException;
}
