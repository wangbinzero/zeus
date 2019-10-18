package com.study.zeus.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateUtil {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");


    public static String format(LocalDateTime ldt) {
        return ldt.format(DTF);
    }

    public static long format(Date date){
        return date.getTime();
    }
}
