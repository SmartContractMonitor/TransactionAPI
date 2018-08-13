package txnapi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {

    /**
     * Converts yyyy-MM-dd date (e.g. 2018-08-29) to UNIX Timestamp.
     * @param date date as yyyy-MM-dd string
     * @return timestamp
     */
    public static Long timestampForDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date parsedDate = format.parse(date);
            return parsedDate.getTime() / 1000;
        } catch (ParseException | NullPointerException e) {
            return null;
        }
    }
}