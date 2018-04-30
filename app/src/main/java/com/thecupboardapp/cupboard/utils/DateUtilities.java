package com.thecupboardapp.cupboard.utils;

import java.text.SimpleDateFormat;

public class DateUtilities {
    public static String longToDate(long dateInMillis) {
        return SimpleDateFormat.getDateInstance(SimpleDateFormat.MEDIUM).format(dateInMillis);
    }
}
