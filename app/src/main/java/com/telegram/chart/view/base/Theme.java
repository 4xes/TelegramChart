package com.telegram.chart.view.base;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;

import com.telegram.chart.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Theme {
    private @ThemeId int id;
    private int primaryColor;
    private int primaryDarkColor;
    private int backgroundWindowColor;
    private int backgroundSecondColor;
    private int dividerColor;
    private int axisValueColor;
    private int axisColor;
    private int nameColor;

    public Theme(Context context, @ThemeId int id, int primaryColor, int primaryDarkColor, int backgroundWindowColor, int backgroundSecondColor, int dividerColor, int axisValueColor, int axisColor, int nameColor) {
        this.id = id;
        this.primaryColor = ContextCompat.getColor(context, primaryColor);
        this.primaryDarkColor = ContextCompat.getColor(context, primaryDarkColor);
        this.backgroundWindowColor = ContextCompat.getColor(context, backgroundWindowColor);
        this.backgroundSecondColor = ContextCompat.getColor(context, backgroundSecondColor);
        this.dividerColor = ContextCompat.getColor(context, dividerColor);
        this.axisValueColor = ContextCompat.getColor(context, axisValueColor);
        this.axisColor = ContextCompat.getColor(context, axisColor);
        this.nameColor = ContextCompat.getColor(context, nameColor);
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public @ColorRes int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public @ColorRes int getBackgroundWindowColor() {
        return backgroundWindowColor;
    }

    public @ColorRes int getBackgroundSecondColor() {
        return backgroundSecondColor;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public int getAxisValueColor() {
        return axisValueColor;
    }

    public int getAxisColor() {
        return axisColor;
    }

    public int getNameColor() {
        return nameColor;
    }

    public static Theme createTheme(Context context, @ThemeId int themeId) {
        switch (themeId) {
            case DAY:
                return createDay(context);
            case NIGHT:
                return createNight(context);
        }
        return createDay(context);
    }

    private static Theme createNight(Context context) {
        return new Theme(
                context,
                DAY,
                R.color.primary_night,
                R.color.primary_dark_night,
                R.color.background_night,
                R.color.background_second_night,
                R.color.divider_night,
                R.color.axis_value_night,
                R.color.axis_night,
                R.color.column_name_night
        );
    }

    private static Theme createDay(Context context) {
        return new Theme(
                context,
                DAY,
                R.color.primary_day,
                R.color.primary_dark_day,
                R.color.background_day,
                R.color.background_second_day,
                R.color.divider_day,
                R.color.axis_value_day,
                R.color.axis_day,
                R.color.column_name_day
        );
    }

    public static final int DAY = 0;
    public static final int NIGHT = 1;

    @IntDef({DAY, NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ThemeId { }
}
