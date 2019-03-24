package com.telegram.chart.view.theme;

import android.content.Context;
import androidx.annotation.ColorRes;
import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

import com.telegram.chart.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Theme {
    private @ThemeId int id;
    private int primaryColor;
    private int primaryDarkColor;
    private int backgroundWindowColor;
    private int backgroundSpacingColor;
    private int dividerColor;
    private int axisValueColor;
    private int axisColor;
    private int nameColor;
    private int rangeColor;
    private int rangeSelectedColor;
    private int backgroundInfoColor;

    public Theme(Context context, @ThemeId int id, @ColorRes int primaryColor, @ColorRes  int primaryDarkColor, @ColorRes  int backgroundWindowColor, @ColorRes  int backgroundSpacingColor, @ColorRes  int dividerColor, @ColorRes  int axisValueColor, @ColorRes  int axisColor, @ColorRes  int nameColor, @ColorRes  int rangeColor, @ColorRes  int rangeSelectedColor, @ColorRes  int backgroundInfoColor) {
        this.id = id;
        this.primaryColor = ContextCompat.getColor(context, primaryColor);
        this.primaryDarkColor = ContextCompat.getColor(context, primaryDarkColor);
        this.backgroundWindowColor = ContextCompat.getColor(context, backgroundWindowColor);
        this.backgroundSpacingColor = ContextCompat.getColor(context, backgroundSpacingColor);
        this.dividerColor = ContextCompat.getColor(context, dividerColor);
        this.axisValueColor = ContextCompat.getColor(context, axisValueColor);
        this.axisColor = ContextCompat.getColor(context, axisColor);
        this.nameColor = ContextCompat.getColor(context, nameColor);
        this.rangeColor = ContextCompat.getColor(context, rangeColor);
        this.rangeSelectedColor = ContextCompat.getColor(context, rangeSelectedColor);
        this.backgroundInfoColor = ContextCompat.getColor(context, backgroundInfoColor);
    }

    public int getPrimaryColor() {
        return primaryColor;
    }

    public int getPrimaryDarkColor() {
        return primaryDarkColor;
    }

    public int getBackgroundWindowColor() {
        return backgroundWindowColor;
    }

    public int getBackgroundSpacingColor() {
        return backgroundSpacingColor;
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

    public int getRangeColor() {
        return rangeColor;
    }

    public int getRangeSelectedColor() {
        return rangeSelectedColor;
    }

    public int getBackgroundInfoColor() {
        return backgroundInfoColor;
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
                NIGHT,
                R.color.primary_night,
                R.color.primary_dark_night,
                R.color.background_night,
                R.color.background_spacing_night,
                R.color.divider_night,
                R.color.axis_value_night,
                R.color.axis_night,
                R.color.column_name_night,
                R.color.range_night,
                R.color.range_selected_night,
                R.color.info_bg_night
        );
    }

    private static Theme createDay(Context context) {
        return new Theme(
                context,
                DAY,
                R.color.primary_day,
                R.color.primary_dark_day,
                R.color.background_day,
                R.color.background_spacing_day,
                R.color.divider_day,
                R.color.axis_value_day,
                R.color.axis_day,
                R.color.column_name_day,
                R.color.range_day,
                R.color.range_selected_day,
                R.color.info_bg_day
        );
    }

    public static final int DAY = 0;
    public static final int NIGHT = 1;

    @IntDef({DAY, NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeId { }
}
