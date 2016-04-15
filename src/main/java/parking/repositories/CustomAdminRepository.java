package parking.repositories;

import parking.beans.document.Account;

import java.util.List;

/**
 * Created by Lina on 15/04/16.
 */
public interface CustomAdminRepository {
    List<Account> getAllUsernames();
}
