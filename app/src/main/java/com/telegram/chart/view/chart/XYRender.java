package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import androidx.collection.LongSparseArray;

import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

class XYRender implements Themable {
    private final Graph graph;
    private final TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final LongSparseArray<String> sparseArray = new LongSparseArray<>();
    private final Paint linePaint = new Paint();
    private final float[] values = new float[3];
    private final float valueHeight;

    public XYRender(Graph data) {
        this.graph = data;
        initPaints();
        valueHeight = measureHeightText(valuePaint);
    }

    private void initPaints() {
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setTextSize(pxFromSp(11f));
        valuePaint.setTextAlign(Paint.Align.LEFT);

        linePaint.setStrokeWidth(pxFromDp(1f));
        linePaint.setStyle(Paint.Style.STROKE);
    }



    @Override
    public void applyTheme(Theme theme) {
        linePaint.setColor(theme.getAxisColor());
        valuePaint.setColor(theme.getAxisValueColor());
    }

    public void renderY(Canvas canvas, RectF r) {
        final long max = graph.state.getMaxChart();
        final float step = calculateStep(0f, max, GRID);
        final float percent = graph.state.progressY();
        if (graph.state.previousStep < graph.state.currentStep) {
            renderY(canvas, r, step, graph.state.previousStep,  - (percent), percent);
            renderY(canvas, r, step, step, 1f - (percent), 1f - percent);
        } else {
            renderY(canvas, r, step, graph.state.previousStep, percent, percent);
            renderY(canvas, r, step, step, 1f + percent, 1f - percent);
        }
    }

    public void renderY(Canvas canvas, RectF r, float step, float stepText, float offsetPercentage, float alphaPercentage) {
        final float scaleY = 1f / (graph.state.getMaxChartStepped() / r.height());
        final float offsetY = r.bottom;

        for (int i = 3; i < GRID; i = i + 3) {
            final float y = (-step * (i + (offsetPercentage * 3f)) * scaleY) + offsetY;
            final float valueY = y -(valueHeight / 2f) + valuePaint.descent();
            final int alpha = 255 - Math.round(255f * alphaPercentage);
            if (alpha != 0) {
                valuePaint.setAlpha(alpha);
                linePaint.setAlpha(alpha);
                canvas.drawText(String.valueOf(i * (int) stepText), r.left, valueY, valuePaint);
                canvas.drawLine(r.left, y, r.right, y, linePaint);
            }
        }
        valuePaint.setAlpha(255);
        linePaint.setAlpha(255);
        final float text0Y = offsetY - (valueHeight / 2f) + valuePaint.descent();
        canvas.drawText("0", r.left, text0Y, valuePaint);
        canvas.drawLine(r.left, offsetY, r.right, offsetY, linePaint);

    }

    public void renderVLine(Canvas canvas, RectF r, float x) {
        canvas.drawLine(x, r.top, x, r.bottom, linePaint);
    }

    public static float calculateStep(float start, float end, int grid) {
        long avg = (long) Math.ceil((end - start) / (float) grid);
        if (avg < 1L) {
            avg = 1L;
        }
        float max = (float) Math.floor(start / (float) avg) + (float) (grid - 1) * (float) avg;
        if (max < end) {
            avg++;
        }
        float step = 1f;
        while (avg >= 20) {
            avg /= 10;
            step *= 10f;
        } do {
            max = ((float) Math.floor(start / ((float) avg * step)) + (float)(grid - 1)) * (float) avg * step;
            if (max >= end) {
                break;
            }
            if (max < end) {
                avg++;
            }
        } while (true);
        step *= (float) avg;

        return step;
    }

    public static final int GRID = 18;
}