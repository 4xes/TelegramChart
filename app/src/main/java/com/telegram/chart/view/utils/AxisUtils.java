package com.telegram.chart.view.utils;

import java.util.Locale;

public final class AxisUtils {

    public static String formatAxis(long j, boolean isFormatted) {
        if (j < 1000 || !isFormatted) {
            return String.valueOf(j);
        }
        Locale locale;
        String str;
        if (j < 10000) {
            locale = Locale.getDefault();
            str = "%.001fk";
            return String.format(locale, str,((float) j) / 1000.0f);
        } else if (j < 1000000) {
            locale = Locale.getDefault();
            str = "%dk";
            return String.format(locale, str, (int) (((float) j) / 1000.0f));
        } else if (j < 10000000) {
            locale = Locale.getDefault();
            str = "%.01fm";
            return String.format(locale, str,((float) j) / 1000000.0f);
        } else if (j < 1000000000) {
            locale = Locale.getDefault();
            str = "%dm";
            return String.format(locale, str,(int) (((float) j) / 1000000.0f));
        } else if (j < 10000000000L) {
            locale = Locale.getDefault();
            str = "%.01fb";
            return String.format(locale, str,((float) j) / 1.0E9f);
        } else {
            locale = Locale.getDefault();
            str = "%db";
            return String.format(locale, str,(int) (((float) j) / 1.0E9f));
        }
    }
}
