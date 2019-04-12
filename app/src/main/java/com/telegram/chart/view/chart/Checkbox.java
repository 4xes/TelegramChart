package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.telegram.chart.view.annotation.Nullable;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.reconcileSize;

public class Checkbox extends View {

    protected RectF bound = new RectF();

    public Checkbox(Context context) {
        super(context);
    }

    public Checkbox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Checkbox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minWidth = pxFromDp(200);
        final int minHeight = pxFromDp(30);

        int measuredWidth = reconcileSize(minWidth, widthMeasureSpec);
        int measuredHeight = reconcileSize(minHeight, heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        bound.left = getPaddingLeft();
        bound.top = getPaddingTop();
        bound.right = getWidth() - getPaddingRight();
        bound.bottom = getHeight() - getPaddingBottom();
    }

    protected boolean isReady() {
        return bound.width() > 0;
    }
}