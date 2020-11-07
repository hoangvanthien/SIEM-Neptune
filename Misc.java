package Utilities;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
/**
 * format and parse date/time
 * @author Hoang Van Thien
 */
public class Misc {
    public static String formatTime(long timestamp) {
        Timestamp ts = new Timestamp(timestamp);
        Date date = new Date(ts.getTime());
        DateFormat f = new SimpleDateFormat("dd/MMM/yyyy' 'HH:mm:ss");
        return f.format(date);
    }
}
