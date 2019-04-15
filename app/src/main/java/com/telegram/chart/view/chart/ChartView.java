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
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.R;
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
    protected final RectF zoomOutBound = new RectF();
    private final TextPaint titlePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint zoomPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint datePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private final GradientDrawable gradientDrawable = new GradientDrawable();
    private int[] gradientColors = new int[2];
    private final float horizontalPadding = pxFromDp(1f);
    private TimeAnimator animator;
    private Render render;
    private Render zoomRender;
    private XYRender xyRender;
    private XYRender zoomXYRender;
    private OnShowInfoListener onShowInfoListener;
    private Theme theme;
    private GraphManager manager;
    private float dateOffsetX = pxFromDp(10f);
    private float dateOffsetY = pxFromDp(10f);
    private float dateSize = pxFromSp(13f);
    private float titleSizeText = pxFromSp(15f);
    private String titleText;
    private int selectChartIndex = NONE_INDEX;
    private int selectZoomIndex = NONE_INDEX;
    private Drawable zoomOutDay;
    private Drawable zoomOutNight;
    private Drawable currentZoom;
    private final float zoomOutSize = pxFromDp(24f);
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
        zoomOutDay = context.getResources().getDrawable(R.drawable.ic_zoom_day);
        zoomOutDay.setBounds(0, 0, (int) zoomOutSize, (int) zoomOutSize);
        zoomOutNight = context.getResources().getDrawable(R.drawable.ic_zoom_night);
        zoomOutNight.setBounds(0, 0, (int) zoomOutSize, (int) zoomOutSize);
        currentZoom = zoomOutDay;
    }

    private void initPaints(Context context) {
        titlePaint.setStyle(Paint.Style.FILL);
        titlePaint.setTextSize(titleSizeText);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        zoomPaint.setStyle(Paint.Style.FILL);
        zoomPaint.setTextSize(titleSizeText);
        zoomPaint.setTextAlign(Paint.Align.LEFT);
        zoomPaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

        datePaint.setStyle(Paint.Style.FILL);
        datePaint.setTextSize(pxFromSp(dateSize));
        datePaint.setTextAlign(Paint.Align.RIGHT);
        datePaint.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));

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
        if (zoomRender != null) {
            zoomRender.applyTheme(theme);
        }
        if (xyRender != null) {
            xyRender.applyTheme(theme);
        }
        if (theme.id == Theme.DAY) {
            currentZoom = zoomOutDay;
        } else {
            currentZoom = zoomOutNight;
        }

        gradientColors[0] = theme.contentColor;
        gradientDrawable.setColors(gradientColors);

        titlePaint.setColor(theme.titleColor);
        datePaint.setColor(theme.titleColor);
        zoomPaint.setColor(theme.zoomColor);
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
        zoomOutBound.set(visibleBound.left, bound.top,bound.left + zoomPaint.measureText("Zoom Out") + zoomOutSize * 2f, titleBound.bottom + titleBound.height() / 2f);
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
        float y = event.getY();
        if (zoomOutBound.contains(x, y)) {
            if (manager.zoomManager != null) {
                manager.state.resetZoom(false);
            }
            selectChartIndex = NONE_INDEX;
            onShowInfoListener.hideInfo();
            invalidate();
        }
        if (render != null) {
            GraphManager manager = this.manager.zoomManager != null? this.manager.zoomManager: this.manager;
            boolean isZoom = this.manager.zoomManager != null;
            if (chartBound.top < y && chartBound.bottom > y) {
                int touchIndex = manager.getIndex(x, chartBound);
                if (touchIndex != selectChartIndex) {
                    if (isZoom) {
                        selectZoomIndex = touchIndex;
                    } else {
                        selectChartIndex = touchIndex;
                    }
                    if (onShowInfoListener != null) {
                        if (touchIndex == NONE_INDEX) {
                            onShowInfoListener.hideInfo();
                        } else {
                            manager.calculateLine(touchIndex, chartBound, point);
                            if (manager.chart.isPercentage) {
                                onShowInfoListener.showInfo(touchIndex, percentageBound, point);
                            } else {
                                onShowInfoListener.showInfo(touchIndex, chartBound, point);
                            }
                        }
                    }
                    invalidate();
                }
            } else {
                selectChartIndex = NONE_INDEX;
                onShowInfoListener.hideInfo();
                invalidate();
            }
        }
        return true;
    }

    public void resetIndex() {
        selectChartIndex = NONE_INDEX;
        selectZoomIndex = NONE_INDEX;
        onShowInfoListener.hideInfo();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (manager.zoomManager != null) {
            if (zoomRender == null) {
                zoomRender = RenderFabric.getChart(manager.zoomManager);
                if (zoomXYRender == null) {
                    zoomXYRender = new XYRender(manager.zoomManager);
                }
                if(theme != null) {
                    zoomRender.applyTheme(theme);
                    zoomXYRender.applyTheme(theme);
                }
            }
        } else {
            zoomRender = null;
            zoomXYRender = null;
        }

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw");
        }
        if (theme != null) {
            canvas.drawColor(theme.contentColor);
        }

        //canvas.drawRect(zoomOutBound, debugPaint);

        boolean hasContent = manager.countVisible() > 0;
        if (render != null && (zoomXYRender == null || manager.state.previousZoom != manager.state.currentZoom)) {
            if (manager.chart.isPercentage) {
                render.render(canvas, percentageBound, visibleBound, selectChartIndex);
            } else {
                render.render(canvas, chartBound, visibleBound, selectChartIndex);
            }
        }
        if (zoomRender != null) {
            if (manager.chart.isPercentage) {
                zoomRender.render(canvas, percentageBound, visibleBound, selectZoomIndex);
            } else {
                zoomRender.render(canvas, chartBound, visibleBound, selectZoomIndex);
            }
        }

        if (hasContent) {
            if (xyRender != null && (zoomXYRender == null || manager.state.previousZoom != manager.state.currentZoom)) {
                if (selectChartIndex != NONE_INDEX) {
                    if (manager.chart.isPercentage || manager.chart.isLined) {
                        manager.calculateLine(selectChartIndex, chartBound, point);
                        if (manager.chart.isPercentage) {
                            xyRender.renderVLine(canvas, percentageBound, point.x);
                        } else {
                            xyRender.renderVLine(canvas, chartBound, point.x);
                        }

                    }
                }
                xyRender.renderYLines(canvas, manager.chart.isPercentage? percentageBound : chartBound);
                xyRender.renderXLines(canvas, datesBound, chartBound, visibleBound);
            }

            if (zoomXYRender != null) {
                GraphManager manager = this.manager.zoomManager != null? this.manager.zoomManager: this.manager;
                if (selectZoomIndex != NONE_INDEX) {
                    if (manager.chart.isPercentage || manager.chart.isLined) {
                        manager.calculateLine(selectZoomIndex, chartBound, point);
                        if (manager.chart.isPercentage) {
                            xyRender.renderVLine(canvas, percentageBound, point.x);
                        } else {
                            xyRender.renderVLine(canvas, chartBound, point.x);
                        }

                    }
                }
                zoomXYRender.renderYLines(canvas, manager.chart.isPercentage? percentageBound : chartBound);
                zoomXYRender.renderXLines(canvas, datesBound, chartBound, visibleBound);
            }
        }
        gradientDrawable.draw(canvas);

        renderTitle(canvas);
        renderDates(canvas);
    }

    private void renderDates(Canvas canvas) {
        GraphManager manager = this.manager.zoomManager != null? this.manager.zoomManager: this.manager;
        final float percent = manager.state.progressDate();
        renderDate(canvas, -percent, 1f - percent, manager.state.prevDate);
        renderDate(canvas, 1f - percent, percent, manager.state.currentDate);
    }

    public void renderDate(Canvas canvas, float offsetPercentage, float alphaPercentage, String date) {
        final float offsetX = dateOffsetX * offsetPercentage;
        final float offsetY = dateOffsetY * offsetPercentage;

        int alpha = (int) Math.ceil(255 * alphaPercentage);
        if (alpha != 0) {
            datePaint.setAlpha(alpha);
            datePaint.setTextSize(dateSize * alphaPercentage);
            canvas.drawText(date, titleBound.right + offsetX, titleBound.bottom + offsetY, datePaint);
        }
    }

    private void renderTitle(Canvas canvas) {
        final float percent = manager.state.progressZoom();
        Log.d("percent", String.valueOf(percent));
        renderTitle(canvas, -percent, 1f - percent, manager.state.previousZoom);
        renderTitle(canvas, 1f - percent, percent, manager.state.currentZoom);
    }

    public void renderTitle(Canvas canvas, float offsetPercentage, float alphaPercentage, boolean isZoom) {
        final float offsetX = dateOffsetX * offsetPercentage;
        final float offsetY = dateOffsetY * offsetPercentage;
        final float iconSize = zoomOutSize * alphaPercentage;

        int alpha = (int) Math.ceil(255 * alphaPercentage);
        if (alpha != 0) {
            if (isZoom) {
                zoomPaint.setAlpha(alpha);
                zoomPaint.setTextSize(titleSizeText * alphaPercentage);
                currentZoom.setBounds((int) dateOffsetX, (int) dateOffsetY, (int) dateOffsetX + (int) iconSize, (int) iconSize + (int) dateOffsetY);
                currentZoom.draw(canvas);
                canvas.drawText("Zoom Out", titleBound.left + zoomOutSize * 1.4f + offsetX, titleBound.bottom + offsetY, zoomPaint);
            } else {
                titlePaint.setAlpha(alpha);
                titlePaint.setTextSize(titleSizeText * alphaPercentage);
                canvas.drawText(titleText, titleBound.left + offsetX, titleBound.bottom + offsetY, titlePaint);
            }
        }
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