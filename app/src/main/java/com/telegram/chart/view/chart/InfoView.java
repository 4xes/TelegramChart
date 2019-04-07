package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.chart.Graph.NONE_INDEX;

public class InfoView extends BaseMeasureView implements Themable, ChartView.OnShowInfoListener {
    private InfoRender infoRender;
    private Theme theme;
    protected final RectF drawBound = new RectF();
    private final PointF point = new PointF();
    private int index = NONE_INDEX;

    public InfoView(Context context) {
        super(context);
        init();
    }

    public boolean isShowing() {
        return index != NONE_INDEX;
    }

    public InfoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    public void seGraph(Graph graph) {
        infoRender = new InfoRender(graph, getContext());
        if (theme != null) {
            applyTheme(theme);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        if (infoRender != null) {
            infoRender.applyTheme(theme);
        }
        invalidate();
    }

    @Override
    public void showInfo(int index, RectF bound, PointF point) {
        if (getVisibility() == View.VISIBLE && this.index == index) {
            return;
        }
        setVisibility(View.VISIBLE);
        this.index = index;
        this.drawBound.set(bound);
        this.point.set(point);
        invalidate();
    }

    @Override
    public void hideInfo() {
        index = NONE_INDEX;
        setVisibility(View.GONE);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (infoRender != null && index != NONE_INDEX) {
            infoRender.render(canvas, index, bound, point);
        }
    }
}
