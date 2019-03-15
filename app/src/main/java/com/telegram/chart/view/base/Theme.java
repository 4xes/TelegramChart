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

    public Theme(Context context,@ThemeId int themeId, @ColorRes int primaryColor, @ColorRes int primaryDarkColor, @ColorRes int backgroundWindowColor, @ColorRes int backgroundSecondColor) {
        this.id = themeId;
        this.primaryColor = ContextCompat.getColor(context, primaryColor);
        this.primaryDarkColor = ContextCompat.getColor(context, primaryDarkColor);
        this.backgroundWindowColor = ContextCompat.getColor(context, backgroundWindowColor);
        this.backgroundSecondColor = ContextCompat.getColor(context, backgroundSecondColor);
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

    public static Theme createTheme(Context context, @ThemeId int themeId) {
        switch (themeId) {
            case DAY:
                return new Theme(context, themeId, R.color.primary_day, R.color.primary_dark_day, R.color.background_day, R.color.background_second_day);
            case NIGHT:
                return new Theme(context, themeId, R.color.primary_night, R.color.primary_dark_night, R.color.background_night, R.color.background_second_night);
        }
        return new Theme(context, themeId, R.color.primary_day, R.color.primary_dark_day, R.color.background_day, R.color.background_second_day);
    }

    public static final int DAY = 0;
    public static final int NIGHT = 1;

    @IntDef({DAY, NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface ThemeId { }
}
