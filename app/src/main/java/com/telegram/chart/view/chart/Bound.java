package com.telegram.chart.view.chart;

import android.graphics.Rect;
import android.graphics.RectF;

public class Bound extends RectF {

    public float offsetX;
    public float offsetY;

    public Bound() {
        offsetX = offsetY = 0f;
    }

    public Bound(float left, float top, float right, float bottom) {
        super(left, top, right, bottom);
        offsetX = offsetY = 0f;
    }

    public Bound(RectF r) {
        super(r);
        offsetX = offsetY = 0f;
    }

    public Bound(Rect r) {
        super(r);
        offsetX = offsetY = 0f;
    }

}
