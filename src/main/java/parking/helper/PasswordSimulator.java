package parking.helper;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PasswordSimulator {

    private static List<String> words = Arrays.asList("vejas", "zaibas", "vasara", "pavasaris", "debesys", "ledai", "apelsinas");

    public static String getPassword() {
        Random random = new Random();
        int firstWord = random.nextInt(words.size());
        int secondWord = random.nextInt(words.size());

        // generate random password
        String password = words.get(firstWord) + words.get(secondWord).toUpperCase() + secondWord + firstWord;

        return password;
    }
}
