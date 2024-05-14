package de.dasshorty.dcevents.api;

import java.time.Duration;

/**
 * Class made by DasShorty ~Anthony
 */
public class BotDuration {

    public static long ofMinutes(long minutes) {
        return Duration.ofMinutes(minutes).toMillis();
    }

    public static long ofHours(long hours) {
        return Duration.ofHours(hours).toMillis();
    }

    public static long ofDays(long days) {
        return ofHours(days * 24);
    }

    public static long ofWeeks(long week) {
        return ofDays(week * 7);
    }

    public static long ofMonths(long months) {
        return ofWeeks(months * 4);
    }

    public static long ofYears(long years) {
        return ofMonths(years * 12);
    }

    public static long duration(String duration, long amount) {

        switch (duration) {
            case "m" -> {
                return BotDuration.ofMinutes(amount);
            }
            case "h" -> {
                return BotDuration.ofHours(amount);
            }
            case "M" -> {
                return BotDuration.ofMonths(amount);
            }
            case "Y" -> {
                return BotDuration.ofYears(amount);
            }
            default -> {
                return BotDuration.ofDays(amount);
            }
        }

    }

}
