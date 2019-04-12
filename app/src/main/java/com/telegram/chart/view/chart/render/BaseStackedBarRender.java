package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ColorUtils;

abstract class BaseStackedBarRender extends Render {
    private final Paint paintBars = new Paint();
    private final float[][] drawBars;

    public BaseStackedBarRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);

        final int linePointsLength = manager.chart.x.length * 4;
        drawBars = new float[manager.countLines()][linePointsLength];
        initPaints();
    }

    private void initPaints() {
        paintBars.setStyle(Paint.Style.STROKE);
        paintBars.setStrokeCap(Paint.Cap.SQUARE);
        paintBars.setStrokeWidth(1);
    }

    public void recalculateBars(int lower, int upper) {
        for (int i = lower; i <= upper; i++) {
            int sum = 0;
            for (int id = 0; id < manager.countLines(); id++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                drawBars[id][iX0] = i;
                drawBars[id][iY0] = sum;
                sum -= manager.chart.data[id].y[i];
                drawBars[id][iX1] = drawBars[id][iX0];
                drawBars[id][iY1] = sum;
            }
        }
    }

    protected abstract void updateMatrix(RectF chart);

    public void render(Canvas canvas, RectF chart, RectF visible) {
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        recalculateBars(lower, upper);
        int saveCount = canvas.save();
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ColorUtils.blendARGB(backgroundColor, color[id], currentAlpha);
                paintBars.setColor(blendColor);
                if (isPreview) {
                    canvas.drawLines(drawBars[id], paintBars);
                } else {
                    canvas.drawLines(drawBars[id], lower * 4, (upper - lower) * 4 + 4, paintBars);
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }
}