package com.telegram.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.telegram.chart.view.utils.ViewUtils;

public class SlopScrollView extends ScrollView {

    private float previousX = 0;
    private float previousY = 0;
    private float dx = 0;
    private float dy = 0;

    private float MAX_DX = ViewUtils.pxFromDp(30);
    private float MAX_DY = ViewUtils.pxFromDp(30);
    private boolean blockScrolling = false;
    private boolean doScrolling = false;

    public SlopScrollView(Context context) {
        this(context, null, 0);
    }

    public SlopScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlopScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        final int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                previousX = x;
                previousY = y;
                dx = 0f;
                dy = 0f;
                doScrolling = false;
                blockScrolling = false;
                super.onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                dx += Math.abs(x - previousX);
                dy += Math.abs(y - previousY);
                previousX = x;
                previousY = y;
                if (dx > MAX_DX && !doScrolling) {
                    blockScrolling = true;
                }

                if (dy > MAX_DY && !blockScrolling) {
                    doScrolling = true;
                }

                if (!blockScrolling) {
                    super.onTouchEvent(ev);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(!blockScrolling) {
                    super.onTouchEvent(ev);
                }
                doScrolling = false;
                blockScrolling = false;
                return false;
            default:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);
        return true;
    }
}