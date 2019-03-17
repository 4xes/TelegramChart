package com.telegram.chart.view.range;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.reconcileSize;

public abstract class BaseRangeView extends View {

    public BaseRangeView(Context context) {
        super(context);
    }

    public BaseRangeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseRangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BaseRangeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    protected RectF bound = new RectF();
    protected RectF line = new RectF();
    protected RectF selectedRange = new RectF();
    protected RectF fingerLeft = new RectF();
    protected RectF fingerRight = new RectF();

    private Float start = 0.8f;
    private Float end = 1f;
    private Float min = 0.2f;

    private Float halfTouch = pxFromDp(10f);
    private Float xDown = 0f;
    private @Zone int currentZone = NONE;

    private OnRangeListener onRangeListener;

    public OnRangeListener getOnRangeListener() {
        return onRangeListener;
    }

    public void setOnRangeListener(OnRangeListener onRangeListener) {
        this.onRangeListener = onRangeListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minWidth = pxFromDp(200);
        final int minHeight = pxFromDp(30);

        int measuredWidth = reconcileSize(minWidth, widthMeasureSpec);
        int measuredHeight = reconcileSize(minHeight, heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        bound.left = getPaddingLeft();
        bound.top = getPaddingTop();
        bound.right = getWidth() - getPaddingRight();
        bound.bottom = getHeight() - getPaddingBottom();

        line.set(bound);
        selectedRange.set(line);
        fingerLeft.set(line);
        fingerRight.set(line);
        recalculateBounds();
    }


    private void recalculateBounds() {
        selectedRange.left = line.left + (line.width() * start);
        selectedRange.right = line.left + (line.width() * end);

        fingerLeft.left = selectedRange.left - halfTouch;
        fingerLeft.right = selectedRange.left + halfTouch;


        fingerRight.left = selectedRange.right - halfTouch;
        fingerRight.right = selectedRange.right + halfTouch;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final float x = event.getX();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentZone = getZone(x);
                xDown = x;
                return true;
            case MotionEvent.ACTION_MOVE:
                float dx = x - xDown;
                xDown = x;
                handleMove(dx);
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        return false;
    }


    private void handleMove(float dx) {
        final float oldStart = start;
        final float oldEnd = end;
        final float dPercentage = dx / line.width();

        switch (currentZone) {
            case START:
                if (isNeedMoveLeft(dPercentage)) {
                    moveRange(dPercentage);
                } else {
                    moveStart(dPercentage);
                }
                break;
            case RANGE:
                moveRange(dPercentage);
                break;
            case END:
                if (isNeedMoveRight(dPercentage)) {
                    moveRange(dPercentage);
                } else {
                    moveEnd(dPercentage);
                }
                break;
            case NONE:
                break;
        }

        final boolean isChange = oldStart != start || oldEnd != end;
        if (isChange) {
            onChange();
        }
    }

    private boolean isNeedMoveLeft(Float dPercentage) {
        final float range = end - (start + dPercentage);
        return range < min;
    }

    private boolean isNeedMoveRight(Float dPercentage) {
        final float range = (end + dPercentage) - start;
        return range < min;
    }

    private void moveStart(Float dPercentage) {
        start += dPercentage;
        if (start < 0f) {
            start = 0f;
        } else if (end - start < min) {
            start = end - min;
        }
    }

    private void moveRange(Float dPercentage) {
        final float range = end - start;
        final boolean toRight = dPercentage > 0f;
        if (toRight) {
            end += dPercentage;
            if (end > 1f) {
                end = 1f;
            }
            start = end - range;
        } else {
            start += dPercentage;
            if (start < 0f) {
                start = 0f;
            }
            end = start + range;
        }
    }

    private void moveEnd(Float dPercentage) {
        end += dPercentage;
        if (end > 1f) {
            end = 1f;
        } else if (end - start < min) {
            if (start + min > 1f) {
                start = 1f - min;
                end = 1f;
            } else {
                end = start + min;
            }
        }
    }

    private void onChange() {
        recalculateBounds();
        notifyListeners();
        invalidate();
    }

    private @Zone int getZone(Float x) {
        if (fingerLeft.left <=  x && fingerLeft.right >= x) {
            return START;
        }
        if (fingerRight.left <=  x && fingerRight.right >= x) {
            return END;
        }
        if (selectedRange.left <= x && selectedRange.right >= x) {
            return RANGE;
        }
        return NONE;
    }

    public void setValues(Float start, Float end) {
        this.start = start;
        this.end = end;
        invalidate();
    }

    private void notifyListeners() {
        if (onRangeListener != null) {
            onRangeListener.onChangeRange(start, end);
        }
    }

    public Float getStart() {
        return start;
    }

    public Float getEnd() {
        return end;
    }

    public interface OnRangeListener {
        void onChangeRange(Float start, Float end);
    }

    private static final int START = 0;
    private static final int RANGE = 1;
    private static final int END = 2;
    private static final int NONE = 3;

    @IntDef({START, RANGE, END, NONE})
    @Retention(RetentionPolicy.SOURCE)
    @interface Zone { }
}