package kr.plurly.daily.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtil {

    private static final String[] SUFFIXES = {

            //   0     1     2     3     4     5     6     7     8     9
                "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //  10    11    12    13    14    15    16    17    18    19
                "th", "th", "th", "th", "th", "th", "th", "th", "th", "th",
            //  20    21    22    23    24    25    26    27    28    29
                "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th",
            //  30    31
                "th", "st"
    };

    private static final DateFormat EVENT_MONTH_FORMAT = new SimpleDateFormat("MMM", Locale.getDefault());
    private static final DateFormat EVENT_DATE_FORMAT = new SimpleDateFormat("yyyy. M. d", Locale.getDefault());
    private static final DateFormat EVENT_TIME_FORMAT = new SimpleDateFormat("h : m a", Locale.getDefault());
    private static final DateFormat EVENT_DAY_FORMAT = new SimpleDateFormat("d", Locale.getDefault());

    public static String getDate(Date date) { return date != null ? EVENT_DATE_FORMAT.format(date) : null; }
    public static String getTime(Date date) { return date != null ? EVENT_TIME_FORMAT.format(date) : null; }
    public static String getMonth(Date date) { return date != null ? EVENT_MONTH_FORMAT.format(date).toUpperCase() : null; }

    public static String getDay(Date date) {

        if (date == null)
            return null;

        int day = Integer.parseInt(EVENT_DAY_FORMAT.format(date));
        return day + SUFFIXES[day];
    }
}
