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
import android.view.View;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.chart.Graph.NONE_INDEX;
import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class ChartView extends BaseMeasureView implements Themable<Theme>, Graph.InvalidateListener {

    protected final Bound chartBound = new Bound();
    protected final Bound datesBound = new Bound();
    private final TextPaint valuesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint datesPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint dividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private boolean debug = false;
    private final float horizontalPadding = pxFromDp(1f);
    private final float verticalPadding = pxFromDp(2f);

    private List<LineRender> lineRenders = new ArrayList<>();
    private OnShowInfoListener onShowInfoListener;
    private Theme theme;
    private Graph graph;
    private int selectIndex = NONE_INDEX;

    public void setOnShowInfoListener(OnShowInfoListener onShowInfoListener) {
        this.onShowInfoListener = onShowInfoListener;
    }

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
        this.theme = theme;
        valuesPaint.setColor(theme.getAxisValueColor());
        datesPaint.setColor(theme.getAxisValueColor());
        dividerPaint.setColor(theme.getDividerColor());
        for (LineRender renderer: lineRenders) {
            renderer.setWindowColor(theme.getBackgroundWindowColor());
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

    public void seGraph(Graph graph) {
        this.graph = graph;
        lineRenders.clear();
        lineRenders = graph.initRenders();
        for (LineRender renderer: lineRenders) {
            renderer.setLineWidth(pxFromDp(2f));
        }
        graph.addListener(this);
        if (theme != null){
            applyTheme(theme);
        }
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        chartBound.set(bound);
        datesBound.set(bound);
        datesBound.top = bound.bottom - measureHeightText(datesPaint);
        chartBound.bottom = datesBound.top;

        chartBound.bottom -= verticalPadding * 2f;
        chartBound.right -= horizontalPadding * 2f;
        chartBound.offsetX = horizontalPadding;
        chartBound.offsetY = verticalPadding;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        if (lineRenders.size() > 0) {
            int touchIndex = graph.getIndex(x, chartBound);
            if (touchIndex != selectIndex) {
                selectIndex = touchIndex;
                if (onShowInfoListener != null) {
                    graph.calculateLine(selectIndex, chartBound, point);
                    onShowInfoListener.showInfo(selectIndex, chartBound, point);
                }
                invalidate();
            }
        }
        return true;
    }

    public void resetIndex() {
        selectIndex = NONE_INDEX;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int save = canvas.save();
        canvas.clipRect(bound);
        for (LineRender render: lineRenders) {
            render.render(canvas, chartBound);
        }
        canvas.restoreToCount(save);
        if (selectIndex != NONE_INDEX) {
            graph.calculateLine(selectIndex, chartBound, point);
            canvas.drawLine(point.x, chartBound.top, point.x, datesBound.top, dividerPaint);
            for (LineRender render: lineRenders) {
                render.renderCircle(canvas, selectIndex, chartBound);
            }
        }
        if (debug) {
            int index = graph.getIndex(chartBound.left, chartBound);
            graph.calculateLine(index, chartBound, point);
            canvas.drawLine(point.x, chartBound.top, point.x, datesBound.top, dividerPaint);
            index = graph.getIndex(chartBound.right, chartBound);
            graph.calculateLine(index, chartBound, point);
            canvas.drawLine(point.x, chartBound.top, point.x, datesBound.top, dividerPaint);
        }
        canvas.drawLine( bound.left, datesBound.top, bound.right, datesBound.top, dividerPaint);
    }

    @Override
    public void needInvalidate() {
        invalidate();
    }

    public interface OnShowInfoListener {
        void showInfo(int index, RectF bound, PointF point);
    }
}