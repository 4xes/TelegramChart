package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.utils.ViewUtils;

import static com.telegram.chart.view.utils.ViewUtils.clipSupport;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class ChartRangeView extends BaseRangeView implements Themable<Theme> {

    private Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float selectVerticalWidth = pxFromDp(1f);
    private float selectHorizontalWidth = pxFromDp(4f);

    public ChartRangeView(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ChartRangeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartRangeView(@Nullable Context context) {
        super(context);
    }

    @Override
    public void applyTheme(Theme theme) {
        rangePaint.setColor(theme.getRangeColor());
        selectedPaint.setColor(theme.getRangeSelectedColor());
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int saveRange = canvas.save();
        clipSupport(canvas, selectedRange);
        canvas.drawRect(line, rangePaint);
        canvas.restoreToCount(saveRange);
        final int saveSelected = canvas.save();
        clipSupport(canvas, selectedRange.left + selectHorizontalWidth, selectedRange.top + selectVerticalWidth, selectedRange.right - selectHorizontalWidth, selectedRange.bottom - selectVerticalWidth);
        canvas.drawRect(selectedRange, selectedPaint);
        canvas.restoreToCount(saveSelected);
    }
}