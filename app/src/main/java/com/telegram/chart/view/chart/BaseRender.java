package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public abstract class BaseRender implements Themable {
    protected final int id;
    protected final GraphManager manager;
    protected final float[] matrixArray = new float[4];
    protected final Matrix matrix = new Matrix();

    public BaseRender(int id, GraphManager graphManager) {
        this.id = id;
        this.manager = graphManager;
    }

    public abstract void render(Canvas canvas, RectF chart, RectF visible);

    @Override
    public void applyTheme(Theme theme) {

    }


    public void renderSelect(Canvas canvas, int index, RectF chart, RectF visible) {}
}
