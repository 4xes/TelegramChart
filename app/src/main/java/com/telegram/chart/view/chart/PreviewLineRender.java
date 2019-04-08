package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class PreviewLineRender extends BaseRender {
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final float[] lines;
    public final float[] drawLines;
    public final int lineColor;

    public PreviewLineRender(int id, GraphManager manager) {
        super(id, manager);
        lineColor = manager.chart.data[id].color;
        final int linePointsLength = 4 + (manager.chart.x.length - 2) * 4;
        lines = new float[linePointsLength];
        drawLines = new float[linePointsLength];
        initPaints();
        initDrawArrays();
    }

    private void initPaints() {
        final float stroke = pxFromDp(1f);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(lineColor);
        paintLine.setStrokeWidth(stroke);
        paintLine.setStrokeCap(Paint.Cap.SQUARE);
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

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible) {
        float currentAlpha = manager.state.chart.alphaCurrent[id];
        int alpha = (int) Math.ceil(255 * currentAlpha);
        if (alpha != 0) {
            calculate(chart);
            paintLine.setAlpha((int) Math.ceil(255 * currentAlpha));
            canvas.drawLines(drawLines, paintLine);
        }
    }

    private void calculate(RectF r) {
        manager.matrixPreview(id, r, matrix);
        matrix.mapPoints(drawLines, lines);
    }
}