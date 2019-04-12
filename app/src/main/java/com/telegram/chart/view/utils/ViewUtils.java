package com.telegram.chart.view.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
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
        return (float) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, Resources.getSystem().getDisplayMetrics()));
    }

    public static float pxFromSp(float value) {
        return (float) Math.ceil(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, Resources.getSystem().getDisplayMetrics()));
    }

    public static float measureHeightText(final Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.bottom - fm.top + fm.leading;
    }

    public static void clipSupport(Canvas canvas, RectF rect) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(rect);
        } else {
            canvas.clipRect(rect, Region.Op.DIFFERENCE);
        }
    }

    public static void clipSupport(Canvas canvas, Path path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutPath(path);
        } else {
            canvas.clipPath(path, Region.Op.DIFFERENCE);
        }
    }

    public static void drawRoundRectSupport(Canvas canvas, Paint paint, RectF rect, Path path, float radius) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(rect, radius, radius, paint);
        } else {
            path.reset();
            path.addRoundRect(rect, radius, radius, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }
    }

    public static void clipSupport(Canvas canvas, float left, float top, float right, float bottom) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(left, top, right, bottom);
        } else {
            canvas.clipRect(left, top, right, bottom, Region.Op.DIFFERENCE);
        }
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

    public static int blendARGB(int color1, int color2, float ratio) {
        final float inverseRatio = 1 - ratio;
        float a = Color.alpha(color1) * inverseRatio + Color.alpha(color2) * ratio;
        float r = Color.red(color1) * inverseRatio + Color.red(color2) * ratio;
        float g = Color.green(color1) * inverseRatio + Color.green(color2) * ratio;
        float b = Color.blue(color1) * inverseRatio + Color.blue(color2) * ratio;
        return Color.argb((int) a, (int) r, (int) g, (int) b);
    }

    public static int getColor(Context context, int resId) {
        if (Build.VERSION.SDK_INT >= 23) {
            return context.getColor(resId);
        } else {
            return context.getResources().getColor(resId);
        }
    }
}
