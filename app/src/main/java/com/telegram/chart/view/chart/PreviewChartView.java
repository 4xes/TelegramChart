package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class PreviewChartView extends BaseMeasureView implements Graph.InvalidateListener {

    private final Bound chartBound = new Bound();
    private final float horizontalPadding = pxFromDp(1f);
    private final float verticalPadding = pxFromDp(2f);
    private List<LineRender> lineRenders = new ArrayList<>();
    private Graph chartData = null;

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

    public void seGraph(Graph graph) {
        lineRenders.clear();
        lineRenders = graph.initRenders();
        graph.addListener(this);
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
    public void needInvalidate() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (LineRender render: lineRenders) {
            render.renderPreview(canvas, chartBound);
        }
    }

}