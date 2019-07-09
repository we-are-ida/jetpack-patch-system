package be.ida.jetpack.patchsystem.utils;

import be.ida.jetpack.patchsystem.models.PatchResult;
import be.ida.jetpack.patchsystem.models.PatchStatus;

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

    public static String formattedRunningTime(PatchResult patchResult) {

        Calendar startDate = patchResult.getStartDate();
        Calendar endDate = patchResult.getEndDate();

        if (PatchStatus.RUNNING.isOfStatus(patchResult) && endDate == null) {
            endDate = Calendar.getInstance();
        }

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
