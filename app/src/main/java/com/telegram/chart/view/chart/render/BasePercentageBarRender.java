package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ViewUtils;

abstract class BasePercentageBarRender extends Render {
    protected final Paint paintBars = new Paint();
    protected final Path[] drawPaths;

    public BasePercentageBarRender(GraphManager manager) {
        super(manager);
        drawPaths = new Path[manager.countLines()];
        for (int id = 0; id < manager.countLines(); id++) {
            drawPaths[id] = new Path();
        }
        initPaints();
    }

    protected void initPaints() {
        paintBars.setStyle(Paint.Style.FILL);
    }

    public void recalculateBars(RectF chart, int lower, int upper) {
        final float height = chart.height();
        for (int id = 0; id < manager.countLines(); id++) {
            drawPaths[id].reset();
            drawPaths[id].moveTo(upper, 0);
            drawPaths[id].lineTo(upper, 0);
            drawPaths[id].lineTo(lower, 0);
        }
        for (int i = lower; i <= upper; i++) {
            float sum = 0;
            float sumPoint = 0;

            for (int id = 0; id < manager.countLines(); id++) {
                sumPoint += manager.chart.data[id].y[i];
            }

            for (int id = 0; id < manager.countLines(); id++) {
                sum -= Math.ceil(manager.chart.data[id].y[i] / (sumPoint/ height));
                if (sum < -height) {
                    sum = -height;
                }
                drawPaths[id].lineTo(i, sum);
            }
        }

        for (int id = 0; id < manager.countLines(); id++) {
            drawPaths[id].close();
            drawPaths[id].transform(matrix);
        }
    }

    protected abstract int getLower(RectF chart, RectF visible);
    protected abstract int getUpper(RectF chart, RectF visible);

    protected abstract void updateMatrix(RectF chart);

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible) {
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        recalculateBars(chart, lower, upper);
        for (int id = manager.countLines() - 1; id >= 0; id--) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ViewUtils.blendARGB(backgroundColor, color[id], currentAlpha);
                paintBars.setColor(blendColor);
                canvas.drawPath(drawPaths[id], paintBars);
            }
        }
    }
}