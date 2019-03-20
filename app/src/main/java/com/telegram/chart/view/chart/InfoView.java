package com.telegram.chart.view.chart;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;

import static com.telegram.chart.view.chart.Graph.NONE_INDEX;

public class InfoView extends BaseMeasureView implements Themable<Theme> {
    private InfoRender infoRender;
    private Theme theme;
    protected final RectF drawBound = new Bound();
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

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void seGraph(Graph graph) {
        infoRender = new InfoRender(graph);
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

    public void hide() {
        index = NONE_INDEX;
        setVisibility(View.GONE);
        invalidate();
    }

    public void showInfo(int index, RectF bound, PointF point) {
        setVisibility(View.VISIBLE);
        this.index = index;
        this.drawBound.set(bound);
        this.point.set(point);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (infoRender != null && index != NONE_INDEX) {
            infoRender.render(canvas, index, bound, point);
        }
    }
}
