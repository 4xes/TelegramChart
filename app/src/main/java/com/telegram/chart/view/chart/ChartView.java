package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.utils.ViewUtils;

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
    private final float chartPadding = pxFromDp(2f);

    private List<LineRenderer> lineRenders = new ArrayList<>();
    private ChartData chartData = null;
    private float start = 0.8f;
    private float end = 1f;

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
        invalidate();
    }

    private void init(Context context) {
        valuesPaint.setTextSize(pxFromSp(9f));
        valuesPaint.setTextAlign(Paint.Align.LEFT);

        datesPaint.setTextSize(pxFromSp(9f));
        datesPaint.setTextAlign(Paint.Align.CENTER);

        dividerPaint.setStrokeWidth(pxFromDp(1f));
    }

    public void setChartData(ChartData chartData) {
        lineRenders.clear();
        this.chartData = chartData;
        if (chartData != null) {
            for (LineData lineData: chartData.getLines()) {
                lineRenders.add(new LineRenderer(lineData));
            }
            computeRenders();
        }
        invalidate();
    }

    public void setVisible(float start, float end, boolean init) {
        this.start = start;
        this.end = end;
        if (!init) {
            for (LineRenderer render: lineRenders) {
                render.changeMatrix(start, end);
            }
        }
        invalidate();
    }

    private void computeRenders() {
        if (isReady() && chartData != null) {
            for (LineRenderer render: lineRenders) {
                render.calculatePath(chartBound, chartData.getMaxY(), chartData.getMinY(), start, end);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // Measure maximum possible width of text.
        // Estimate maximum possible height of text.
        Paint.FontMetrics fontMetrics = datesPaint.getFontMetrics();
        float maxTextHeight = fontMetrics.bottom - fontMetrics.top;

        chartBound.set(bound);
        datesBound.set(bound);
        datesBound.top = bound.bottom - maxTextHeight;
        chartBound.bottom = datesBound.top;

        chartBound.bottom -= chartPadding * 2f;
        chartBound.right -= chartPadding * 2f;
        chartBound.offsetX = chartPadding;
        chartBound.offsetY = chartPadding;
        computeRenders();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineRenderer render: lineRenders) {
            render.render(chartBound, canvas);
        }
        canvas.drawLine(bound.left, chartBound.bottom, bound.right, chartBound.bottom, dividerPaint);
    }

}