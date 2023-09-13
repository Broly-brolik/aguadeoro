package com.aguadeoro.utils;

import java.time.format.DateTimeFormatter;

public class Constants {
    public static DateTimeFormatter DateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    public static DateTimeFormatter fromAccessFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static DateTimeFormatter fromAccessFormatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
