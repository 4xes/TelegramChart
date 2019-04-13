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
    public final int contentColor;
    public final int windowColor;
    public final int gridColor;
    public final int rangeColor;
    public final int rangeSelectedColor;
    public final int tooltipColor;
    public final int shadowTop;
    public final int shadowBottom;
    public final int axisX;
    public final int axisY;
    public final int axisStackedX;
    public final int axisStackedY;
    public final int mask;

    public Theme(Context context, @ThemeId int id, int actionBar, String actionBarColorTitle, int titleColor, int contentColor, int windowColor, int gridColor, int rangeColor, int rangeSelectedColor, int tooltipColor, int shadowTop, int shadowBottom, int axisX, int axisY, int axisStackedX, int axisStackedY, int mask) {
        this.id = id;
        this.actionBar = getColor(context, actionBar);
        this.actionBarColorTitle = actionBarColorTitle;
        this.titleColor = getColor(context, titleColor);
        this.contentColor = getColor(context, contentColor);
        this.windowColor = getColor(context, windowColor);
        this.gridColor = getColor(context, gridColor);
        this.rangeColor = getColor(context, rangeColor);
        this.rangeSelectedColor = getColor(context, rangeSelectedColor);
        this.tooltipColor = getColor(context, tooltipColor);
        this.shadowTop = shadowTop;
        this.shadowBottom = shadowBottom;
        this.axisX = getColor(context, axisX);
        this.axisY = getColor(context, axisY);
        this.axisStackedX = getColor(context, axisStackedX);
        this.axisStackedY = getColor(context, axisStackedY);
        this.mask = getColor(context, mask);
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
                R.color.content_night,
                R.color.window_night,
                R.color.grid_lines_night,
                R.color.scroll_night,
                R.color.scroll_selected_night,
                R.color.tooltip_bg_night,
                R.drawable.shadow_top_night,
                R.drawable.shadow_bottom_night,
                R.color.axis_x_night,
                R.color.axis_y_night,
                R.color.axis_x_stacked_night,
                R.color.axis_y_stacked_night,
                R.color.mask_day
        );
    }

    private static Theme createDay(Context context) {
        return new Theme(
                context,
                DAY,
                R.color.toolbar_day,
                "#000000",
                R.color.toolbar_title_day,
                R.color.content_day,
                R.color.window_day,
                R.color.grid_lines_day,
                R.color.scroll_day,
                R.color.scroll_selected_day,
                R.color.tooltip_bg_day,
                R.drawable.shadow_top_day,
                R.drawable.shadow_bottom_day,
                R.color.axis_x_day,
                R.color.axis_y_day,
                R.color.axis_x_stacked_day,
                R.color.axis_y_stacked_day,
                R.color.mask_night
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
