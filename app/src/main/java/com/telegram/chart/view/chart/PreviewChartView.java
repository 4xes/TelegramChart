package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.data.Chart;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.render.Render;
import com.telegram.chart.view.chart.render.RenderFabric;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.chart.GraphManager.NONE_INDEX;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class PreviewChartView extends BaseMeasureView implements Themable, GraphManager.InvalidateListener {

    private final RectF chartBound = new RectF();
    private final float horizontalPadding = pxFromDp(1f);
    private final float verticalPadding = pxFromDp(2f);
    private Render render;
    private GraphManager manager;
    public static final String TAG = PreviewChartView.class.getSimpleName();
    private Theme theme;

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

    public void setGraph(GraphManager manager) {
        this.manager = manager;
        render = RenderFabric.getPreview(manager);
        chartBound.set(bound);
        if (manager.chart.type.equals(Chart.TYPE_LINE) || manager.chart.type.equals(Chart.TYPE_LINE_SCALED)) {
            chartBound.inset(horizontalPadding, verticalPadding);
        }
        if (manager.chart.type.equals(Chart.TYPE_BAR_STACKED)) {
            chartBound.top = bound.top + verticalPadding;
        }
        manager.registerView(getViewId(), this);
        if (theme != null){
            applyTheme(theme);
        }
        onChangeBounds();
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        if (render != null) {
            render.applyTheme(theme);
        }
        invalidate();
    }

    private void onChangeBounds() {
        if (manager != null) {
            chartBound.set(bound);
            switch (manager.chart.type) {
                case Chart.TYPE_LINE:
                    chartBound.inset(horizontalPadding, verticalPadding);
                    break;
                case Chart.TYPE_LINE_SCALED:
                    chartBound.inset(horizontalPadding, verticalPadding);
                    break;
                case Chart.TYPE_BAR_STACKED:
                    chartBound.top = bound.top + verticalPadding;
                    break;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        onChangeBounds();
    }

    @Override
    public void needInvalidate() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw");
        }

        if (theme != null) {
            canvas.drawColor(theme.contentColor);
        }


        if (render != null) {
            render.render(canvas, chartBound, null, NONE_INDEX);
        }
    }

    @Override
    public int getViewId() {
        return Ids.PREVIEW;
    }
}