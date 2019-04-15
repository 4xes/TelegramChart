package com.telegram.chart.view.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.chart.GraphManager.NONE_INDEX;

public class TooltipView extends BaseMeasureView implements Themable, ValueAnimator.AnimatorUpdateListener, ChartView.OnShowInfoListener {
    private TooltipRender infoRender;
    private Theme theme;
    protected final RectF drawBound = new RectF();
    private int index = NONE_INDEX;
    private ValueAnimator animator = null;
    private GraphManager manager;
    private OnZoomListener listener;

    public TooltipView(Context context) {
        super(context);
        init();
    }

    public boolean isShowing() {
        return index != NONE_INDEX;
    }

    public TooltipView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TooltipView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    interface OnZoomListener {
        void onZoom();
    }

    public void setListener(OnZoomListener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();
        final float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (infoRender != null) {
                    if (infoRender.infoRect.contains(x, y)) {
                        if (index != NONE_INDEX) {
                            if (manager.zoomManager == null) {
                                manager.onZoom(index);
                            }
                            hideInfo();
                        }
                        return true;
                    }
                }
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }

    public void seGraph(GraphManager manager) {
        this.manager = manager;
        infoRender = new TooltipRender(manager, getContext());

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
        this.drawBound.set(bound);
        if (getVisibility() == View.VISIBLE && this.index == index) {
            return;
        }
        this.index = index;
        if (getVisibility() == View.VISIBLE) {
            //animate
            if (infoRender != null) {
                infoRender.measure(drawBound);
                float x = infoRender.getDrawPosition(drawBound, point);
                toggleAnimate(x);
            }
        } else {
            setVisibility(View.VISIBLE);
        }
        if (infoRender != null) {
            infoRender.measure(drawBound);
            float x = infoRender.getDrawPosition(drawBound, point);
            infoRender.setPosition(x);
        }
        invalidate();
    }

    private float animationX = -1;
    private void toggleAnimate(float x) {
        if (animationX == x && animator != null && animator.isRunning()) {
            return;
        }
        animationX = x;
        ValueAnimator animator = this.animator;
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(infoRender.getPosition(), x);
        animator.addUpdateListener(this);
        animator.start();
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
            infoRender.measure(drawBound);
            infoRender.render(canvas, index);
        }
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        float position  = (float) animation.getAnimatedValue();
        infoRender.setPosition(position);
        invalidate();
    }
}
