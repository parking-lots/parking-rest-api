package parking.helper;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ToolHelper {

    public static Date getCurrentDate(){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date today = new Date();

        try {
            today = formatter.parse(formatter.format(today));
        } catch (ParseException e) {}

        return today;
    }
}
