package com.telegram.chart.view.theme;

import android.content.Context;

import com.telegram.chart.R;
import com.telegram.chart.view.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.telegram.chart.view.utils.ViewUtils.getColor;

public class Theme {
    private @ThemeId int id;
    private int toolbarColor;
    private int titleColor;
    private int backgroundWindowColor;
    private int backgroundSpacingColor;
    private int dividerColor;
    private int axisValueColor;
    private int axisColor;
    private int nameColor;
    private int rangeColor;
    private int rangeSelectedColor;
    private int backgroundInfoColor;

    public Theme(Context context, @ThemeId int id, int toolbarColor, int titleColor, int backgroundWindowColor, int backgroundSpacingColor, int dividerColor, int axisValueColor, int axisColor, int nameColor, int rangeColor, int rangeSelectedColor, int backgroundInfoColor) {
        this.id = id;
        this.toolbarColor = getColor(context, toolbarColor);
        this.titleColor = getColor(context, titleColor);
        this.backgroundWindowColor = getColor(context, backgroundWindowColor);
        this.backgroundSpacingColor = getColor(context, backgroundSpacingColor);
        this.dividerColor = getColor(context, dividerColor);
        this.axisValueColor = getColor(context, axisValueColor);
        this.axisColor = getColor(context, axisColor);
        this.nameColor = getColor(context, nameColor);
        this.rangeColor = getColor(context, rangeColor);
        this.rangeSelectedColor = getColor(context, rangeSelectedColor);
        this.backgroundInfoColor = getColor(context, backgroundInfoColor);
    }

    public int getId() {
        return id;
    }

    private static Theme createNight(Context context) {
        return new Theme(
                context,
                NIGHT,
                R.color.toolbar_night,
                R.color.toolbar_title_night,
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
                R.color.toolbar_day,
                R.color.toolbar_title_day,
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

    public int getToolbarColor() {
        return toolbarColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public static final int DAY = 0;
    public static final int NIGHT = 1;

    @IntDef({DAY, NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeId { }
}
