package com.telegram.chart.view.theme;

import android.content.Context;

import com.telegram.chart.R;
import com.telegram.chart.view.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.telegram.chart.view.utils.ViewUtils.getColor;

public class Theme {
    public final @ThemeId int id;
    public final int actionBar;
    public final String actionBarColorTitle;
    public final int titleColor;
    public final int backgroundWindowColor;
    public final int backgroundSpacingColor;
    public final int dividerColor;
    public final int axisValueColor;
    public final int axisColor;
    public final int nameColor;
    public final int rangeColor;
    public final int rangeSelectedColor;
    public final int tooltipColor;
    public final int shadowTop;
    public final int shadowBottom;

    public Theme(Context context, @ThemeId int id, int actionBar, String actionBarColorTitle, int titleColor, int backgroundWindowColor, int backgroundSpacingColor, int dividerColor, int axisValueColor, int axisColor, int nameColor, int rangeColor, int rangeSelectedColor, int tooltipColor, int shadowTop, int shadowBottom) {
        this.id = id;
        this.actionBar = getColor(context, actionBar);
        this.actionBarColorTitle = actionBarColorTitle;
        this.titleColor = getColor(context, titleColor);
        this.backgroundWindowColor = getColor(context, backgroundWindowColor);
        this.backgroundSpacingColor = getColor(context, backgroundSpacingColor);
        this.dividerColor = getColor(context, dividerColor);
        this.axisValueColor = getColor(context, axisValueColor);
        this.axisColor = getColor(context, axisColor);
        this.nameColor = getColor(context, nameColor);
        this.rangeColor = getColor(context, rangeColor);
        this.rangeSelectedColor = getColor(context, rangeSelectedColor);
        this.tooltipColor = getColor(context, tooltipColor);
        this.shadowTop = shadowTop;
        this.shadowBottom = shadowBottom;
    }

    public int getId() {
        return id;
    }

    private static Theme createNight(Context context) {
        return new Theme(
                context,
                NIGHT,
                R.color.toolbar_night,
                "#FFFFFF",
                R.color.toolbar_title_night,
                R.color.background_night,
                R.color.background_spacing_night,
                R.color.divider_night,
                R.color.axis_value_night,
                R.color.axis_night,
                R.color.column_name_night,
                R.color.range_night,
                R.color.range_selected_night,
                R.color.tooltip_bg_night,
                R.drawable.shadow_top_night,
                R.drawable.shadow_bottom_night
        );
    }

    private static Theme createDay(Context context) {
        return new Theme(
                context,
                DAY,
                R.color.toolbar_day,
                "#000000",
                R.color.toolbar_title_day,
                R.color.background_day,
                R.color.background_spacing_day,
                R.color.divider_day,
                R.color.axis_value_day,
                R.color.axis_day,
                R.color.column_name_day,
                R.color.range_day,
                R.color.range_selected_day,
                R.color.tooltip_bg_day,
                R.drawable.shadow_top_day,
                R.drawable.shadow_bottom_day
        );
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

    public static final int DAY = 0;
    public static final int NIGHT = 1;

    @IntDef({DAY, NIGHT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface ThemeId { }
}
