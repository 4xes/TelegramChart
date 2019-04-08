package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class StackedBarRender extends BaseRender {
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final float[] lines;
    public final float[] drawLines;
    public final int color;

    public StackedBarRender(int id, GraphManager manager) {
        super(id, manager);
        color = manager.chart.data[id].color;
        final int linePointsLength = 4 + (manager.chart.x.length - 2) * 4;
        lines = new float[linePointsLength];
        drawLines = new float[linePointsLength];
        initPaints();
        initDrawArrays();
    }

    private void initPaints() {
        final float stroke = pxFromDp(2f);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(color);
        paintLine.setStrokeWidth(stroke);
        paintLine.setStrokeCap(Paint.Cap.BUTT);
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setColor(color);
        paintPoint.setStrokeCap(Paint.Cap.ROUND);
        paintPoint.setStrokeWidth(stroke);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setColor(color);
        paintInsideCircle.setStyle(Paint.Style.FILL);
    }

    @Override
    public void applyTheme(Theme theme) {
    }

    public void initDrawArrays() {
        int[] y = manager.chart.data[id].data;

        if (y.length > 0) {
            for (int i = 0; i < y.length - 1; i++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                lines[iX0] = i;
                lines[iY0] = -y[i];
                lines[iX1] = (i + 1) ;
                lines[iY1] = -y[i + 1];
            }
        }
    }

    public void recalculateLines(RectF r, int lower, int upper) {
        manager.matrix(id, r, matrix);
        matrix.mapPoints(drawLines, lower * 4, lines, lower * 4, (upper - lower) * 2);
    }

    public void render(Canvas canvas, RectF chart, RectF visible) {
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
            if (upper > maxIndex){
                upper = maxIndex;
            }
            recalculateLines(chart, lower, upper);
            paintLine.setStrokeCap(Paint.Cap.SQUARE);
            paintLine.setAlpha((int) Math.ceil(255 * currentAlpha));
            canvas.drawLines(drawLines, lower * 4, (upper - lower) * 4, paintLine);
        }
    }

    public static StackedBarRender[] createRender(GraphManager manager) {
        StackedBarRender[] renders = new StackedBarRender[manager.countLines()];
        for (int id = 0; id < manager.countLines(); id++) {
            StackedBarRender render = new StackedBarRender(id, manager);
            renders[id] = render;
        }
        return renders;
    }
}