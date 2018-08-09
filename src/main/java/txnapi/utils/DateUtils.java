package txnapi.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateUtils {
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

    public static void main(String[] args) {
        System.out.println(Integer.parseInt("kf"));
    }
}