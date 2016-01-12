package parking.helper;


import org.jasypt.util.password.StrongPasswordEncryptor;

public class ProfileHelper {

    public static String encryptPassword(String password) {
        return new StrongPasswordEncryptor().encryptPassword(password);
    }

    public static Boolean checkPassword(String password, String encrypted) {
        return new StrongPasswordEncryptor().checkPassword(password, encrypted);
    }
}
