package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ViewUtils;

class PreviewPercentageBarRender extends Render {
    private final Paint paintBars = new Paint();
    private final float[][] drawBars;

    public PreviewPercentageBarRender(GraphManager manager) {
        super(manager);

        final int linePointsLength = manager.chart.x.length * 4;
        drawBars = new float[manager.countLines()][linePointsLength];
        initPaints();
    }

    private void initPaints() {
        paintBars.setStyle(Paint.Style.STROKE);
        paintBars.setStrokeCap(Paint.Cap.SQUARE);
        paintBars.setStrokeWidth(1);
    }

    public void recalculateBars(RectF chart) {
        final float height = chart.height();
        for (int i = 0; i < manager.chart.x.length; i++) {
            float sum = 0;
            float sumPoint = 0;

            for (int id = 0; id < manager.countLines(); id++) {
                sumPoint += manager.chart.data[id].y[i];
            }

            for (int id = 0; id < manager.countLines(); id++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                drawBars[id][iX0] = i;
                drawBars[id][iY0] = sum;
                sum -= Math.ceil(manager.chart.data[id].y[i] / (sumPoint/ height));
                if (sum < - height) {
                    sum = - height;
                }
                drawBars[id][iX1] = drawBars[id][iX0];
                drawBars[id][iY1] = sum;
            }
        }
    }

    public void render(Canvas canvas, RectF chart, RectF visible) {
        recalculateBars(chart);
        int saveCount = canvas.save();
        manager.matrixPercentagePreviewBars(chart, matrix);
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ViewUtils.blendARGB(backgroundColor, color[id], currentAlpha);
                paintBars.setColor(blendColor);
                canvas.drawLines(drawBars[id], paintBars);
            }
        }
        canvas.restoreToCount(saveCount);
    }
}