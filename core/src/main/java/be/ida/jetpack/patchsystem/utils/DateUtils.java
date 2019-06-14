package be.ida.jetpack.patchsystem.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author michael
 * @since 2019-06-14
 */
public class DateUtils {

    public static String formattedRunningTime(Calendar startDate, Calendar endDate) {
        if (startDate != null && endDate != null) {
            long end = endDate.getTimeInMillis();
            long start = startDate.getTimeInMillis();
            long diff = Math.abs(end - start);

            Date date = new Date(diff);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss.SSS");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return formatter.format(date);
        }
        return null;
    }

}
