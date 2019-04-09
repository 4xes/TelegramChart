package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public abstract class Render implements Themable {
    final GraphManager manager;
    protected final float[] matrixArray = new float[5];
    final Matrix matrix = new Matrix();

    public Render(GraphManager graphManager) {
        this.manager = graphManager;
    }

    public abstract void render(Canvas canvas, RectF chart, RectF visible);

    @Override
    public void applyTheme(Theme theme) {

    }


    public void renderSelect(Canvas canvas, int index, RectF chart, RectF visible) {}

    public static final int SCALE_X = 0;
    public static final int SCALE_Y = 1;
    public static final int OFFSET_X = 2;
    public static final int OFFSET_Y = 3;
    public static final int SCALED_WIDTH = 4;
}
