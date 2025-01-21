package net.syphlex.practice.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MathUtil {
    public String convertTicksToMinutes(int ticks) {
        long minute = ticks / 1200;
        long second = (long)(ticks / 20) - minute * 60L;
        String secondString = "" + Math.round(second);
        if (second < 10L) {
            secondString = "0" + secondString;
        }
        String minuteString = "" + Math.round(minute);
        if (minute == 0L) {
            minuteString = "0";
        }
        return minuteString + ":" + secondString;
    }
}
