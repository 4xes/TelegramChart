package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public abstract class BaseRender implements Themable {
    final GraphManager manager;
    protected final float[] matrixArray = new float[4];
    final Matrix matrix = new Matrix();

    public BaseRender(GraphManager graphManager) {
        this.manager = graphManager;
    }

    public abstract void render(Canvas canvas, RectF chart, RectF visible);

    @Override
    public void applyTheme(Theme theme) {

    }


    public void renderSelect(Canvas canvas, int index, RectF chart, RectF visible) {}
}
