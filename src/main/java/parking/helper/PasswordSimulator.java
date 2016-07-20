package parking.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PasswordSimulator {
    public static String getPassword() {
        List<String> predefinedPasswords = new ArrayList<>();
        predefinedPasswords.add("vejaS05");
        predefinedPasswords.add("zaiBas10");
        predefinedPasswords.add("krioklyS31");
        predefinedPasswords.add("grioVys89");
        predefinedPasswords.add("deBesys18");

        Random random = new Random();
        //random int in the range of [0-5)
        int randomNum = random.nextInt(5);

        return predefinedPasswords.get(randomNum);
    }
}
