package xyz.jcdc.nasiraba;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jcdc on 1/29/18.
 */

public class Helpah {
    public static String getCurrentDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MMMMM dd, y");
        Date date = new Date();

        return dateFormat.format(date);
    }
}
