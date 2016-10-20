package kr.plurly.daily.util;

import java.util.Calendar;
import java.util.Date;

public class Constraints {

    public static final Date DEFAULT_FETCH_DATE;

    static {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(0);

        DEFAULT_FETCH_DATE = calendar.getTime();
    }

    public static final long WRITE_TIME = 2000;
    public static final long READ_TIME = 500;
    public static final long CONNECTION_TIME = 3000;


    public static final String PART_NAME_PHOTO = "photo";

    public static final String MIME_TYPE_TEXT = "text/plain";
    public static final String MIME_TYPE_IMAGE = "image/jpeg";
}
