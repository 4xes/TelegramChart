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

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class PreviewChartView extends BaseChartView {

    private Bound chartBound = new Bound();
    private final float horizontalPadding = pxFromDp(1f);
    private final float verticalPadding = pxFromDp(2f);
    private List<LineRenderer> lineRenders = new ArrayList<>();
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
                lineRenders.add(new LineRenderer(lineData, pxFromDp(1f)));
            }
        }
        invalidate();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        chartBound.set(bound);
        chartBound.bottom -= verticalPadding * 2f;
        chartBound.right -= horizontalPadding * 2f;
        chartBound.offsetX = horizontalPadding;
        chartBound.offsetY = verticalPadding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineRenderer render: lineRenders) {
            render.render(canvas, chartBound, 0, 1f, chartData.getMaxY());
        }
    }

}