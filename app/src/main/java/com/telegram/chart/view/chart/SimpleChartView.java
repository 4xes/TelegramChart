package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;

import java.util.ArrayList;
import java.util.List;

public class SimpleChartView extends BaseChartView {

    private List<LineRenderer> lineRenders = new ArrayList<>();
    private ChartData chartData = null;

    public SimpleChartView(Context context) {
        super(context);
    }

    public SimpleChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SimpleChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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

    private void computeRenders() {
        if (isReady() && chartData != null) {
            for (LineRenderer render: lineRenders) {
                render.calculatePath(bound, chartData.getMaxY(), chartData.getMinY());
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        computeRenders();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineRenderer render: lineRenders) {
            render.render(canvas);
        }
    }

}