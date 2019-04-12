package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ViewUtils;

abstract class BaseBarRender extends Render {
    private final Paint paintBars = new Paint();
    protected final float[] drawBars;

    public BaseBarRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);
        drawBars = new float[manager.chart.x.length * 4];
        initPaints();
        calculateBars();
    }

    private void initPaints() {
        paintBars.setStyle(Paint.Style.STROKE);
        paintBars.setStrokeWidth(1);
        paintBars.setStrokeCap(Paint.Cap.BUTT);
    }

    public void calculateBars() {
        for (int i = 0; i < manager.chart.x.length; i++) {
            final int iX0 = i * 4;
            final int iY0 = i * 4 + 1;
            final int iX1 = i * 4 + 2;
            final int iY1 = i * 4 + 3;
            drawBars[iX0] = i;
            drawBars[iY0] = 0;
            drawBars[iX1] = i;
            drawBars[iY1] = -manager.chart.data[0].y[i];
        }
    }

    protected abstract void updateMatrix(RectF chart);

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible) {
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        int saveCount = canvas.save();
        manager.matrixBar(chart, matrix);
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ViewUtils.blendARGB(backgroundColor, color[0], currentAlpha);
                paintBars.setColor(blendColor);
                if (isPreview) {
                    canvas.drawLines(drawBars, paintBars);
                } else {
                    canvas.drawLines(drawBars, lower * 4, (upper - lower) * 4 + 4, paintBars);
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }
}