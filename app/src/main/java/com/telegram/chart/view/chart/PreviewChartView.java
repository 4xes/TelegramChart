package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.telegram.chart.BuildConfig;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class PreviewChartView extends BaseMeasureView implements Graph.InvalidateListener {

    private final RectF chartBound = new RectF();
    private final float horizontalPadding = pxFromDp(1f);
    private final float verticalPadding = pxFromDp(2f);
    private List<LineRender> lineRenders = new ArrayList<>();

    public PreviewChartView(Context context) {
        super(context);
        init();
    }

    public PreviewChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PreviewChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public void setGraph(Graph graph) {
        lineRenders.clear();
        lineRenders = LineRender.createListRenderPreview(graph);
        graph.addListener(this);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        chartBound.set(bound);
        chartBound.inset(horizontalPadding, verticalPadding);
    }

    @Override
    public void needInvalidate() {
        if (BuildConfig.DEBUG) {
            Log.d("PreviewChartView", "needInvalidate");
        }
        //setDrawingCacheEnabled(false);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.e("invalidate", "invalidate preview");
        for (LineRender render: lineRenders) {
            render.renderPreview(canvas, chartBound);
        }

        //setDrawingCacheEnabled(true);
    }

    @Override
    public int getViewId() {
        return Ids.PREVIEW;
    }
}