package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class ChartView extends BaseChartView implements Themable<Theme> {

    protected Bound chartBound = new Bound();
    protected RectF datesBound = new RectF();
    private TextPaint valuesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private TextPaint datesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private PointF point = new PointF();
    private final float horizontalPadding = pxFromDp(0f);
    private final float verticalPadding = pxFromDp(2f);
    private int windowColor = 0;

    private List<LineRenderer> lineRenders = new ArrayList<>();
    private ChartData chartData = null;
    private float start = 0.8f;
    private float end = 1f;
    private int selectIndex = NONE_INDEX;

    public ChartView(Context context) {
        super(context);
        init(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    @Override
    public void applyTheme(Theme theme) {
        valuesPaint.setColor(theme.getAxisValueColor());
        datesPaint.setColor(theme.getAxisValueColor());
        dividerPaint.setColor(theme.getDividerColor());
        windowColor = theme.getBackgroundWindowColor();
        for (LineRenderer renderer: lineRenders) {
            renderer.setWindowColor(windowColor);
        }
        invalidate();
    }

    private void init(Context context) {
        valuesPaint.setTextSize(pxFromSp(9f));
        valuesPaint.setTextAlign(Paint.Align.LEFT);

        datesPaint.setTextSize(pxFromSp(9f));
        datesPaint.setTextAlign(Paint.Align.CENTER);

        dividerPaint.setStrokeWidth(pxFromDp(1f));
        dividerPaint.setStyle(Paint.Style.STROKE);
    }

    public void setChartData(ChartData chartData) {
        lineRenders.clear();
        this.chartData = chartData;
        if (chartData != null) {
            for (LineData lineData: chartData.getLines()) {
                LineRenderer renderer = new LineRenderer(lineData);
                renderer.setLineWidth(pxFromDp(2f));
                renderer.setWindowColor(windowColor);
                lineRenders.add(renderer);
            }
        }
        invalidate();
    }

    public void setVisible(float start, float end) {
        this.start = start;
        this.end = end;
        selectIndex = NONE_INDEX;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Paint.FontMetrics fontMetrics = datesPaint.getFontMetrics();
        float maxTextHeight = fontMetrics.bottom - fontMetrics.top;

        chartBound.set(bound);
        datesBound.set(bound);
        datesBound.top = bound.bottom - maxTextHeight;
        chartBound.bottom = datesBound.top;

        chartBound.bottom -= verticalPadding * 2f;
        chartBound.right -= horizontalPadding * 2f;
        chartBound.offsetX = horizontalPadding;
        chartBound.offsetY = verticalPadding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (lineRenders.size() > 0) {
            LineRenderer renderer = lineRenders.get(0);
            int touchIndex = renderer.getIndex(event.getX(), bound, start, end);
            if (touchIndex != selectIndex) {
                selectIndex = touchIndex;
                renderer.calculatePoint(selectIndex, chartBound, start, end, chartData.getMaxY(), point);
                invalidate();
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int save = canvas.save();
        canvas.clipRect(bound);
        for (LineRenderer render: lineRenders) {
            render.render(canvas, chartBound, start, end, chartData.getMaxY());
        }
        canvas.restoreToCount(save);
        if (selectIndex != NONE_INDEX) {
            canvas.drawLine(point.x, chartBound.top, point.x, chartBound.bottom, dividerPaint);
            for (LineRenderer render: lineRenders) {
                render.renderCircle(canvas, selectIndex, chartBound, start, end, chartData.getMaxY());
            }
        }
        //canvas.drawRect(chartBound, dividerPaint);
        canvas.drawLine(bound.left, chartBound.bottom, bound.right, chartBound.bottom, dividerPaint);
    }

    private static final int NONE_INDEX = -1;
}