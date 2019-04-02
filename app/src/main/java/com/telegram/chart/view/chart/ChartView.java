package com.telegram.chart.view.chart;

import android.animation.TimeAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.chart.Graph.NONE_INDEX;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class ChartView extends BaseMeasureView implements Themable, Graph.InvalidateListener, TimeAnimator.TimeListener {

    protected final RectF chartBound = new RectF();
    protected final RectF visibleBound = new RectF();
    protected final RectF datesBound = new RectF();
    protected final RectF clipBound = new RectF();
    Paint paint = new Paint();
    private final PointF point = new PointF();
    private final float horizontalPadding = pxFromDp(1f);
    private TimeAnimator animator;

    private List<LineRender> lineRenders = new ArrayList<>();
    private XYRender xyRender;
    private OnShowInfoListener onShowInfoListener;
    private Theme theme;
    private Graph graph;
    private int selectIndex = NONE_INDEX;

    public void setOnShowInfoListener(OnShowInfoListener onShowInfoListener) {
        this.onShowInfoListener = onShowInfoListener;
    }

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        if (xyRender != null) {
            xyRender.applyTheme(theme);
        }
        for (LineRender renderer: lineRenders) {
            renderer.applyTheme(theme);
        }
        invalidate();
    }


    public void seGraph(Graph graph) {
        this.graph = graph;
        lineRenders.clear();
        lineRenders = LineRender.createListRender(graph);
        xyRender = new XYRender(graph);
        graph.addListener(this);
        if (theme != null){
            applyTheme(theme);
        }
        paint.setStyle(Paint.Style.STROKE);
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        visibleBound.set(0, 0, getWidth(), getHeight());
        chartBound.set(bound);
        datesBound.set(bound);
        datesBound.top = bound.bottom - pxFromDp(28f);
        chartBound.bottom = datesBound.top;
        chartBound.inset(horizontalPadding, 0f);

        clipBound.set(bound.left, 0f, bound.right, getHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
//        if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            getParent().requestDisallowInterceptTouchEvent(true);
//        }
        if (lineRenders.size() > 0) {
            int touchIndex = graph.getIndex(x, bound);
            if (touchIndex != selectIndex) {
                selectIndex = touchIndex;
                if (onShowInfoListener != null) {
                    if (selectIndex == NONE_INDEX) {
                        onShowInfoListener.hideInfo();
                    } else {
                        graph.calculateLine(selectIndex, chartBound, point);
                        onShowInfoListener.showInfo(selectIndex, chartBound, point);
                    }
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
        canvas.clipRect(clipBound);

        boolean hasContent = graph.countVisible() > 0;
        if (xyRender != null && hasContent) {
            xyRender.renderYLines(canvas, chartBound);
        }
        for (LineRender render: lineRenders) {
            render.render(canvas, chartBound);
        }
        canvas.restoreToCount(save);

        if (hasContent) {
            if (xyRender != null) {
                if (selectIndex != NONE_INDEX) {
                    graph.calculateLine(selectIndex, chartBound, point);
                    xyRender.renderVLine(canvas, chartBound, point.x);
                }
                xyRender.renderYText(canvas, chartBound);
                xyRender.renderXLines(canvas, datesBound, chartBound, visibleBound);
            }
        }
        if (xyRender != null) {
            xyRender.renderY0TextAndLine(canvas,chartBound);
        }

        if (selectIndex != NONE_INDEX) {
            for (LineRender render: lineRenders) {
                render.renderCircle(canvas, selectIndex, chartBound);
            }
        }

    }

    @Override
    public void needInvalidate() {
        postInvalidate();
    }

    public interface OnShowInfoListener {
        void showInfo(int index, RectF bound, PointF point);

        void hideInfo();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onSubscribe();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onDescribe();
    }

    public void onSubscribe() {
        onDescribe();
        if (animator == null) {
            animator = new TimeAnimator();
            animator.setTimeListener(this);
            animator.start();
        }
    }

    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut()) {
                return;
            }
        }
        if (isReady() && graph != null) {
            graph.onTimeUpdate(deltaTime);
        }
    }

    public void onDescribe() {
        if (animator != null) {
            animator.cancel();
            animator.setTimeListener(null);
            animator.removeAllListeners();
            animator = null;
        }
    }

    @Override
    public int getViewId() {
        return Ids.CHART;
    }
}