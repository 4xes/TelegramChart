package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender extends Render {
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private final float radius = pxFromDp(4);
    private final float[][] points;
    private final float[][] drawPoints;
    private final float[][] lines;
    private final float[][] drawLines;

    public LineRender(GraphManager manager) {
        super(manager);
        final int pointsLength = manager.chart.x.length * 2;
        final int linePointsLength = 4 + (manager.chart.x.length - 2) * 4;
        lines = new float[manager.countLines()][linePointsLength];
        drawLines = new float[manager.countLines()][linePointsLength];
        points = new float[manager.countLines()][pointsLength];
        drawPoints = new float[manager.countLines()][pointsLength];

        initPaints();
        initDrawArrays();
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

    @Override
    public void applyTheme(Theme theme) {
        int backgroundColor = theme.getBackgroundWindowColor();
        paintInsideCircle.setColor(backgroundColor);
    }

    public void initDrawArrays() {
        for (int id = 0; id < manager.countLines(); id++) {
            int[] y = manager.chart.data[id].y;

            if (y.length > 0) {
                for (int i = 0; i < y.length - 1; i++) {
                    final int iX0 = i * 4;
                    final int iY0 = i * 4 + 1;
                    final int iX1 = i * 4 + 2;
                    final int iY1 = i * 4 + 3;
                    lines[id][iX0] = i;
                    lines[id][iY0] = -y[i];
                    lines[id][iX1] = (i + 1);
                    lines[id][iY1] = -y[i + 1];

                    points[id][i * 2] = i;
                    points[id][(i * 2) + 1] = -y[i];
                }
                final int lastI = (y.length - 1) * 2;
                points[id][lastI] = y.length - 1;
                points[id][lastI + 1] = -y[y.length - 1];
            }
        }
    }

    public void recalculateLines(RectF r, int lower, int upper) {
        for (int id = 0; id < manager.countLines(); id++) {
            manager.matrix(id, r, matrix);
            matrix.mapPoints(drawLines[id], lower * 4, lines[id], lower * 4, (upper - lower) * 2);
            if (upper - lower < 100) {
                matrix.mapPoints(drawPoints[id], lower * 2, points[id], lower * 2, (upper - lower) + 1);
            }
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
                boolean maxOptimize = upper - lower < MAX_OPTIMIZE_LINES;
                if (maxOptimize) {
                    paintLine.setStrokeCap(Paint.Cap.BUTT);
                } else {
                    paintLine.setStrokeCap(Paint.Cap.SQUARE);
                }
                paintLine.setColor(color[id]);
                paintLine.setAlpha((int) Math.ceil(255 * currentAlpha));
                canvas.drawLines(drawLines[id], lower * 4, (upper - lower) * 4, paintLine);
                if (maxOptimize) {
                    paintPoint.setColor(color[id]);
                    paintPoint.setAlpha((int) Math.ceil(255 * currentAlpha));
                    canvas.drawPoints(drawPoints[id], lower * 2, (upper - lower) * 2 + 2, paintPoint);
                }
            }
        }
    }

    @Override
    public void renderSelect(Canvas canvas, int index, RectF chart, RectF visible) {
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                manager.calculatePoint(id, index, chart, point);
                paintCircle.setColor(color[id]);
                paintCircle.setAlpha(alpha);
                canvas.drawCircle(point.x, point.y, radius, paintInsideCircle);
                canvas.drawCircle(point.x, point.y, radius, paintCircle);
            }
        }
    }

    private static final int MAX_OPTIMIZE_LINES = 100;
}