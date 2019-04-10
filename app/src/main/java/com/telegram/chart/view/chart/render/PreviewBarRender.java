package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ViewUtils;

class PreviewBarRender extends Render {
    private final Paint paintBars = new Paint();
    private final float[] drawBars;

    public PreviewBarRender(GraphManager manager) {
        super(manager);
        drawBars = new float[manager.chart.x.length * 4];
        initPaints();
        calculateBars();
    }

    private void initPaints() {
        paintBars.setStyle(Paint.Style.STROKE);
        paintBars.setStrokeWidth(1);
        paintBars.setStrokeCap(Paint.Cap.SQUARE);
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

    public void render(Canvas canvas, RectF chart, @Nullable RectF visible) {
        int saveCount = canvas.save();
        manager.matrixPreview(0, chart, matrix);
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                final int blendColor =  ViewUtils.blendARGB(backgroundColor, color[0], currentAlpha);
                paintBars.setColor(blendColor);
                canvas.drawLines(drawBars, paintBars);
            }
        }
        canvas.restoreToCount(saveCount);
    }
}