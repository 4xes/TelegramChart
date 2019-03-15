package com.telegram.chart.view.utils;

import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

public class ViewUtils {

    public static int pxFromDp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    public static int pxFromSp(int value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, Resources.getSystem().getDisplayMetrics());
    }

    public static float pxFromDp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics());
    }

    public static float pxFromSp(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, Resources.getSystem().getDisplayMetrics());
    }

    public static int reconcileSize(int contentSize, int measureSpec){
        final int mode = View.MeasureSpec.getMode(measureSpec);
        final int specSize = View.MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case View.MeasureSpec.EXACTLY:
                return specSize;
            case View.MeasureSpec.AT_MOST:
                return contentSize < specSize ? contentSize : specSize;
            case View.MeasureSpec.UNSPECIFIED:
                return contentSize;
            default:
                return contentSize;
        }
    }
}
