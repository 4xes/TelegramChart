package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.RectF;

public interface Renderer {
    void render(RectF bound, Canvas canvas);
}
