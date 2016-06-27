package parking.helper;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class ToolHelper {

    public static Date getCurrentDate() {
        Date today = new Date();
        return format(today);
    }

    public static Date formatDate(Date date) {
        return format(date);
    }

    private static Date format(Date date) {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            date = formatter.parse(formatter.format(date));
        } catch (ParseException e) {
        }

        return date;
    }

    public static boolean hasDuplicates(Collection<?> c) {
        Set<Object> set = new HashSet<>();

        for (Object o: c)
            if (!set.add(o))
                return true;

        return false;
    }
}
