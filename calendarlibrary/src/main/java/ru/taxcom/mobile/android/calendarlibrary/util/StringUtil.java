package ru.taxcom.mobile.android.calendarlibrary.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class StringUtil {
    public static Date getUtcNoTime(Date date) {
        return getCalendarUtcNoTime(date).getTime();
    }

    public static Calendar getCalendarUtcNoTime(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar;
    }
}
