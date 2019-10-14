package com.study.zeus.core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtil {

    private static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");


    public static String format(LocalDateTime ldt) {
        return ldt.format(DTF);
    }

}
