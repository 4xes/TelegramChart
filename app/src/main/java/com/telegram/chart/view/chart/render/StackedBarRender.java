package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class StackedBarRender extends Render {
    private final Paint paintBars = new Paint();
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[][] drawBars;
    public final int[] color;

    public StackedBarRender(GraphManager manager) {
        super(manager);
        color = new int[manager.countLines()];
        for (int id = 0; id < manager.countLines(); id++){
            color[id] = manager.chart.data[id].color;
        }
        final int linePointsLength = manager.chart.x.length * 4;
        drawBars = new float[manager.countLines()][linePointsLength];
        initPaints();
    }

    private void initPaints() {
        final float stroke = pxFromDp(2f);
        paintBars.setStyle(Paint.Style.STROKE);
        paintBars.setStrokeWidth(stroke);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintInsideCircle.setStyle(Paint.Style.FILL);
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
        recalculateBars(lower, upper);
        int saveCount = canvas.save();
        manager.matrixStackedBars(chart, matrix);
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                paintBars.setStrokeCap(Paint.Cap.SQUARE);
                paintBars.setColor(color[id]);
                paintBars.setAlpha((int) Math.ceil(255 * currentAlpha));
                paintBars.setStrokeWidth(1);
                canvas.drawLines(drawBars[id], lower * 4, (upper - lower) * 4 + 4, paintBars);
            }
        }
        canvas.restoreToCount(saveCount);
    }
}