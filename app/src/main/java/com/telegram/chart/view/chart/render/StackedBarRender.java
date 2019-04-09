package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class StackedBarRender extends BaseRender {
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[][] lines;
    private final float[][] drawLines;
    private final float[] sumLines;
    public final int[] color;

    public StackedBarRender(GraphManager manager) {
        super(manager);
        color = new int[manager.countLines()];
        for (int id = 0; id < manager.countLines(); id++){
            color[id] = manager.chart.data[id].color;
        }
        final int linePointsLength = manager.chart.x.length * 4;
        lines = new float[manager.countLines()][linePointsLength];
        drawLines = new float[manager.countLines()][linePointsLength];
        sumLines = new float[linePointsLength];
        initPaints();
    }

    private void initPaints() {
        final float stroke = pxFromDp(2f);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(stroke);
        paintLine.setStrokeCap(Paint.Cap.BUTT);
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setStrokeCap(Paint.Cap.ROUND);
        paintPoint.setStrokeWidth(stroke);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintInsideCircle.setStyle(Paint.Style.FILL);
    }

    public void recalculateLines(RectF r, int lower, int upper) {
        for (int id = 0; id < manager.countLines(); id++) {
            int[] y = manager.chart.data[id].data;

            if (y.length > 0) {
                for (int i = 0; i < y.length; i++) {
                    if (id == 0) {
                        sumLines[i] = 0f;
                    }
                    final int iX0 = i * 4;
                    final int iY0 = i * 4 + 1;
                    final int iX1 = i * 4 + 2;
                    final int iY1 = i * 4 + 3;
                    lines[id][iX0] = i;
                    lines[id][iY0] = sumLines[i];
                    lines[id][iX1] = i;
                    sumLines[i] -= y[i];
                    lines[id][iY1] = sumLines[i];
                }
            }
            manager.matrix(id, r, matrix);
            matrix.mapPoints(drawLines[id], lower * 4, lines[id], lower * 4, (upper - lower) * 2);
        }
    }

    public void render(Canvas canvas, RectF chart, RectF visible) {
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
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
                recalculateLines(chart, lower, upper);
                paintLine.setStrokeCap(Paint.Cap.SQUARE);
                paintLine.setColor(color[id]);
                paintLine.setAlpha((int) Math.ceil(255 * currentAlpha));
                canvas.drawLines(drawLines[id], lower * 4, (upper - lower) * 4, paintLine);
            }
        }
    }
}