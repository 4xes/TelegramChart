package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ColorUtils;

abstract class BaseBarRender extends Render {
    protected final float[] drawBars;

    public BaseBarRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);
        drawBars = new float[manager.chart.x.length * 4];
        initPaints();
        calculateBars();
    }

    private void initPaints() {
        for (int id = 0; id <  manager.countLines(); id++) {
            paint[id].setAntiAlias(false);
            paint[id].setStyle(Paint.Style.STROKE);
            paint[id].setStrokeWidth(1);
            paint[id].setStrokeCap(Paint.Cap.BUTT);
        }
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
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ColorUtils.blendARGB(backgroundColor, color[0], currentAlpha);
                paint[id].setColor(blendColor);
                if (isPreview) {
                    canvas.drawLines(drawBars, paint[id]);
                } else {
                    canvas.drawLines(drawBars, lower * 4, (upper - lower) * 4 + 4, paint[id]);
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }
}