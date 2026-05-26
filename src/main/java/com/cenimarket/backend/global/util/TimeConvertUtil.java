package com.cenimarket.backend.global.util;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeConvertUtil {

    public static String convertTime(LocalDateTime targetTime) {

        if(targetTime == null) {
            return "";
        }

        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(targetTime, now);

        long seconds = duration.getSeconds();
        long minutes = duration.toMinutes();
        long hours = duration.toHours();

        if(seconds < 30) {
            return "방금 전";
        } else if(seconds >= 30 && seconds < 59) {
            return duration.getSeconds() + "초 전";
        } else if(minutes < 60) {
            return duration.toMinutes() + "분 전";
        } else if (hours < 24) {
            return duration.toHours() + "시간 전";
        }

        long days = ChronoUnit.DAYS.between(targetTime, now);
        long weeks = ChronoUnit.WEEKS.between(targetTime, now);
        long months = ChronoUnit.MONTHS.between(targetTime, now);
        long years = ChronoUnit.YEARS.between(targetTime, now);

        if(days == 1) {
            return "어제";
        } else if (days < 14) {
            return days + "일 전";
        } else if (weeks >= 2 && months <= 0) {
            return weeks + "주 전";
        } else if (months < 12) {
            return months + "달 전";
        } else {
            return years + "년 전";
        }
    }
}
