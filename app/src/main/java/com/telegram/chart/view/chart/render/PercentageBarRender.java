package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ViewUtils;

class PercentageBarRender extends Render {
    private final Paint paintBars = new Paint();
    private final float[][] drawBars;

    public PercentageBarRender(GraphManager manager) {
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

    public void recalculateBars(RectF chart, int lower, int upper) {
        final float height = chart.height();
        int lastIndexCount = manager.countLines() - 1;
        for (int i = lower; i <= upper; i++) {
            int sum = 0;
            int sumPoint = 0;
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
                drawBars[id][iX1] = drawBars[id][iX0];
                if (id == lastIndexCount) {
                    drawBars[id][iY1] = -height;
                } else {
                    drawBars[id][iY1] = sum;
                }
            }
        }
    }

    public void render(Canvas canvas, RectF chart, RectF visible) {
        final int maxIndex = manager.chart.x.length - 1;
        final float sectionWidth = manager.sectionWidth(chart.width());
        final int addIndexLeft = (int) Math.rint((chart.left - visible.left) / sectionWidth);
        final int addIndexRight = (int) Math.rint((visible.right - chart.right) / sectionWidth);
        int lower = manager.chart.getLower(manager.range.start) - 1 - addIndexLeft;
        if (lower < 0) {
            lower = 0;
        }
        int upper = manager.chart.getUpper(manager.range.end) + 1 + addIndexRight;
        if (upper > maxIndex) {
            upper = maxIndex;
        }
        recalculateBars(chart, lower, upper);
        int saveCount = canvas.save();
        manager.matrixPercentageBars(chart, matrix);
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ViewUtils.blendARGB(backgroundColor, color[id], currentAlpha);
                paintBars.setColor(blendColor);
                canvas.drawLines(drawBars[id], lower * 4, (upper - lower) * 4 + 4, paintBars);
            }
        }
        canvas.restoreToCount(saveCount);
    }
}