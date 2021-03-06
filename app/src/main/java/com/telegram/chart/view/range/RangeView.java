package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.view.annotation.NonNull;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.chart.Ids;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.utils.ViewUtils.clipSupport;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class RangeView extends BaseRangeView implements Themable, GraphManager.InvalidateListener {

    public static final String TAG = RangeView.class.getSimpleName();

    private Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint roundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint markPaint = new Paint();
    private RectF roundRect = new RectF();
    private RectF fingerRect = new RectF();

    private float paddingFinger = pxFromDp(1f);
    private float lineWidth = pxFromDp(2f);
    private float lineHeight = pxFromDp(10f);
    private float[] markPoints = new float[8];
    private float withFinger = pxFromDp(10f);
    private float RANGE_RADIUS = pxFromDp(6f);


    public RangeView(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RangeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RangeView(@Nullable Context context) {
        super(context);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
        initPaint();
    }

    private void initPaint() {
        markPaint.setStrokeWidth(lineWidth);
        markPaint.setColor(Color.WHITE);
        markPaint.setStyle(Paint.Style.STROKE);
        markPaint.setStrokeCap(Paint.Cap.ROUND);

        roundPaint.setStyle(Paint.Style.STROKE);
        roundPaint.setStrokeWidth(RANGE_RADIUS);
    }

    @Override
    public void applyTheme(Theme theme) {
        rangePaint.setColor(theme.rangeColor);
        selectedPaint.setColor(theme.rangeSelectedColor);
        roundPaint.setColor(theme.contentColor);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        roundRect.set(bound);
        roundRect.inset(-RANGE_RADIUS / 2f, -RANGE_RADIUS / 2f);
    }

    @Override
    public void needInvalidate() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "needInvalidate");
        }
        invalidate();
    }

    public void seGraph(GraphManager manager) {
        manager.registerView(getViewId(), this);
        invalidate();
    }

    @Override
    public void recalculateBounds() {
        super.recalculateBounds();
        fingerRect.set(selectedRange);
        selectedRange.inset(withFinger, 0);
        fingerRect.inset(0, -paddingFinger);

        markPoints[0] = fingerRect.left + withFinger / 2f;
        markPoints[1] = fingerRect.centerY() - lineHeight / 2f;
        markPoints[2] = markPoints[0];
        markPoints[3] = fingerRect.centerY() + lineHeight / 2f;

        markPoints[4] = fingerRect.right - withFinger / 2f;
        markPoints[5] = markPoints[1];
        markPoints[6] = markPoints[4];
        markPoints[7] = markPoints[3];
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw");
        }
        final int saveRange = canvas.save();
        clipSupport(canvas, selectedRange);
        canvas.drawRoundRect(line, RANGE_RADIUS, RANGE_RADIUS, rangePaint);
        canvas.drawRoundRect(roundRect, RANGE_RADIUS * 1.5f, RANGE_RADIUS * 1.5f, roundPaint);
        canvas.drawRoundRect(fingerRect, RANGE_RADIUS, RANGE_RADIUS, selectedPaint);
        canvas.drawLines(markPoints, markPaint);
        canvas.restoreToCount(saveRange);
    }

    @Override
    public int getViewId() {
        return Ids.RANGE;
    }
}