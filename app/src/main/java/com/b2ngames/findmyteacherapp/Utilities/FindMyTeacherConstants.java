package com.b2ngames.findmyteacherapp.Utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xavi on 03/06/2017.
 */

public class FindMyTeacherConstants {
    public static String SHARED_PREFERENCES_LOGIN = "prefsLogin";
    public static String IS_LOGED = "login";
    public static final int FILTERS_ACTIVITY = 1001;
    public static final int MAIN_ACTIVITY = 1;
    public static final int TEACHER_INFO_ACTIVITY = 2;
    public static final int TEACHER_AREA_ACTIVITY = 3;
    public static final String CALLING_ACTIVITY_KEY = "callingActivity";
    public static final String TOKEN = "token";
    public static final String TOKEN_DATE = "token_date";
    public static final String TOKEN_DATE_FORMAT = "EEEE, dd/MM/yyyy/hh:mm:ss";
    public static final String MY_SERVER_ID = "myId";
    public static final int TOKEN_SECONDS_TO_UPDATE = 3600*24*3; // 3 Days
    public static final int PENDING_MESSAGE_SEND = 0;
    public static final int MESSAGE_SENT = 1;

    public static boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}
