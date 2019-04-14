package com.telegram.chart.view.chart;

import android.animation.TimeAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.render.Render;
import com.telegram.chart.view.chart.render.RenderFabric;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.chart.GraphManager.NONE_INDEX;
import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class ChartView extends BaseMeasureView implements Themable, GraphManager.InvalidateListener, TimeAnimator.TimeListener {

    protected final RectF chartBound = new RectF();
    protected final RectF percentageBound = new RectF();
    protected final RectF visibleBound = new RectF();
    protected final RectF datesBound = new RectF();
    protected final RectF titleBound = new RectF();
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private final GradientDrawable gradientDrawable = new GradientDrawable();
    private int[] gradientColors = new int[2];
    private final float horizontalPadding = pxFromDp(1f);
    private TimeAnimator animator;
    private Render render;
    private XYRender xyRender;
    private OnShowInfoListener onShowInfoListener;
    private Theme theme;
    private GraphManager manager;
    private String titleText;
    private int selectIndex = NONE_INDEX;
    public static final String TAG = ChartView.class.getSimpleName();

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

    private void init(Context context) {
        setLayerType(View.LAYER_TYPE_HARDWARE, null);

        initPaints(context);
    }

    private void initPaints(Context context) {
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setTextSize(pxFromSp(15f));
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        debugPaint.setStyle(Paint.Style.STROKE);

        gradientColors[1] = Color.TRANSPARENT;
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        if (xyRender != null) {
            xyRender.applyTheme(theme);
        }
        if (render != null) {
            render.applyTheme(theme);
        }
        gradientColors[0] = theme.contentColor;
        gradientDrawable.setColors(gradientColors);

        titlePaint.setColor(theme.titleColor);
        invalidate();
    }

    public void seGraph(GraphManager manager) {
        this.manager = manager;
        render = RenderFabric.getChart(manager);
        xyRender = new XYRender(manager);
        manager.registerView(getViewId(), this);
        if (theme != null){
            applyTheme(theme);
        }
        invalidate();
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        visibleBound.set(0, getPaddingTop(), getWidth(), getHeight() - getPaddingBottom());
        titleBound.set(bound);
        titleBound.bottom = bound.top + measureHeightText(titlePaint) + pxFromDp(8f);
        chartBound.set(bound);
        chartBound.top = bound.top + measureHeightText(titlePaint) + pxFromDp(18f);
        datesBound.set(bound);
        datesBound.top = bound.bottom - pxFromDp(28f);
        chartBound.bottom = datesBound.top;
        chartBound.inset(horizontalPadding, 0f);
        percentageBound.set(chartBound);
        percentageBound.top = percentageBound.top + pxFromDp(16f);
        gradientDrawable.setBounds((int) visibleBound.left, (int) bound.top, (int) visibleBound.right, (int) chartBound.top);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (render != null) {
            int touchIndex = manager.getIndex(x, bound);
            if (touchIndex != selectIndex) {
                selectIndex = touchIndex;
                if (onShowInfoListener != null) {
                    if (selectIndex == NONE_INDEX) {
                        onShowInfoListener.hideInfo();
                    } else {
                        manager.calculateLine(selectIndex, chartBound, point);
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
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw");
        }
        if (theme != null) {
            canvas.drawColor(theme.contentColor);
        }

        boolean hasContent = manager.countVisible() > 0;
        if (render != null) {
            if (manager.chart.isPercentage) {
                render.render(canvas, percentageBound, visibleBound);
            } else {
                render.render(canvas, chartBound, visibleBound);
            }
        }

        if (hasContent) {
            if (xyRender != null) {
                if (selectIndex != NONE_INDEX) {
                    manager.calculateLine(selectIndex, chartBound, point);
                    xyRender.renderVLine(canvas, chartBound, point.x);
                }
                xyRender.renderYLines(canvas, manager.chart.isPercentage? percentageBound : chartBound);
                xyRender.renderXLines(canvas, datesBound, chartBound, visibleBound);
            }
        }

        if (selectIndex != NONE_INDEX && render != null) {
            render.renderSelect(canvas, selectIndex, chartBound, visibleBound);
        }
        gradientDrawable.draw(canvas);
        canvas.drawText(titleText, titleBound.left, titleBound.bottom, titlePaint);
    }

    @Override
    public void needInvalidate() {
        invalidate();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        getParent().requestDisallowInterceptTouchEvent(false);
        return super.dispatchTouchEvent(motionEvent);
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

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onTimeUpdate(TimeAnimator animation, long totalTime, long deltaTime) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!isLaidOut()) {
                return;
            }
        }
        if (isReady() && manager != null) {
            manager.onTimeUpdate(deltaTime);
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