package xyz.jcdc.nasiraba;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by jcdc on 1/29/18.
 */

public class Helpah {
    public static String getCurrentDateString() {
        DateFormat dateFormat = new SimpleDateFormat("MMMM dd, y", Locale.US);
        Date date = new Date();

        return dateFormat.format(date);
    }
}
