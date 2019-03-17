package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

public class PreviewChartView extends BaseChartView {

    private RectF chartBound = new RectF();
    private final float insidePadding = ViewUtils.pxFromDp(2f);
    private List<PreviewRenderer> lineRenders = new ArrayList<>();
    private ChartData chartData = null;

    public PreviewChartView(Context context) {
        super(context);
    }

    public PreviewChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PreviewChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PreviewChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setChartData(ChartData chartData) {
        lineRenders.clear();
        this.chartData = chartData;
        if (chartData != null) {
            for (LineData lineData: chartData.getLines()) {
                lineRenders.add(new PreviewRenderer(lineData));
            }
            computeRenders();
        }
        invalidate();
    }

    private void computeRenders() {
        if (isReady() && chartData != null) {
            for (PreviewRenderer render: lineRenders) {
                render.calculatePath(chartBound, chartData.getMaxY(), chartData.getMinY());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        chartBound.set(bound);
        chartBound.inset(0f, insidePadding);
        computeRenders();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (PreviewRenderer render: lineRenders) {
            render.render(chartBound, canvas);
        }
    }

}