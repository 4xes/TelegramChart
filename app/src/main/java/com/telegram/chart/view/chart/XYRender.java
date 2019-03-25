package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.Log;

import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.DateUtils;



import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

class XYRender implements Themable {
    private final Graph graph;
    private final TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint datePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint();
    private final float valueHeight;
    private final float dateWidth;

    public XYRender(Graph data) {
        this.graph = data;
        initPaints();
        valueHeight = measureHeightText(valuePaint);
        dateWidth = datePaint.measureText(DateUtils.X_FORMAT);
    }

    private void initPaints() {
        valuePaint.setStyle(Paint.Style.FILL);
        valuePaint.setTextSize(pxFromSp(11f));
        valuePaint.setTextAlign(Paint.Align.LEFT);

        datePaint.setStyle(Paint.Style.FILL);
        datePaint.setTextSize(pxFromSp(11f));
        datePaint.setTextAlign(Paint.Align.CENTER);

        linePaint.setStrokeWidth(pxFromDp(1f));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void applyTheme(Theme theme) {
        linePaint.setColor(theme.getAxisColor());
        valuePaint.setColor(theme.getAxisValueColor());
        datePaint.setColor(theme.getAxisValueColor());
    }

    public void renderYLines(Canvas canvas, RectF r) {
        final long max = graph.state.getMaxChart();
        final float step = calculateStep(0f, max, GRID);
        final float percent = graph.state.progressY();
        if (graph.state.previousStep < graph.state.currentStep) {
            renderYLines(canvas, r, step,  - (percent), percent);
            renderYLines(canvas, r, step, 1f - (percent), 1f - percent);
        } else {
            renderYLines(canvas, r, step, percent, percent);
            renderYLines(canvas, r, step, 1f + percent, 1f - percent);
        }
    }

    public void renderYText(Canvas canvas, RectF r) {
        final long max = graph.state.getMaxChart();
        final float step = calculateStep(0f, max, GRID);
        final float percent = graph.state.progressY();
        if (graph.state.previousStep < graph.state.currentStep) {
            renderYText(canvas, r, step, graph.state.previousStep,  - (percent), percent);
            renderYText(canvas, r, step, step, 1f - (percent), 1f - percent);
        } else {
            renderYText(canvas, r, step, graph.state.previousStep, percent, percent);
            renderYText(canvas, r, step, step, 1f + percent, 1f - percent);
        }
    }

    public void renderYLines(Canvas canvas, RectF r, float step, float offsetPercentage, float alphaPercentage) {
        final float scaleY = 1f / (graph.state.getMaxChartStepped() / r.height());
        final float offsetY = r.bottom;

        for (int i = 3; i < GRID; i = i + 3) {
            final float y = (-step * (i + (offsetPercentage * 3f)) * scaleY) + offsetY;
            final int alpha = 255 - Math.round(255f * alphaPercentage);
            if (alpha != 0) {
                linePaint.setAlpha(alpha);
                canvas.drawLine(r.left, y, r.right, y, linePaint);
            }
        }
    }

    public void renderYText(Canvas canvas, RectF r, float step, float stepText, float offsetPercentage, float alphaPercentage) {
        final float offsetY = r.bottom;
            final float scaleY = 1f / (graph.state.getMaxChartStepped() / r.height());
            for (int i = 3; i < GRID; i = i + 3) {
                final float y = (-step * (i + (offsetPercentage * 3f)) * scaleY) + offsetY;
                final float valueY = y -(valueHeight / 2f) + valuePaint.descent();
                final int alpha = 255 - Math.round(255f * alphaPercentage);
                if (alpha != 0) {
                    valuePaint.setAlpha(alpha);
                    canvas.drawText(String.valueOf(i * (int) stepText), r.left, valueY, valuePaint);
                }
            }
            valuePaint.setAlpha(255);
            linePaint.setAlpha(255);
    }

    public void renderY0TextAndLine(Canvas canvas, RectF r) {
        final float text0Y = r.bottom - (valueHeight / 2f) + valuePaint.descent();
        canvas.drawText("0", r.left, text0Y, valuePaint);
        canvas.drawLine(r.left, r.bottom, r.right, r.bottom, linePaint);
    }

    public void renderVLine(Canvas canvas, RectF r, float x) {
        canvas.drawLine(x, r.top, x, r.bottom, linePaint);
    }

    public void renderXLines(Canvas canvas, RectF dateBound, RectF chartBound, RectF visibleBound) {
        final float w = chartBound.width();
        int count = ((int) (w / (dateWidth * 2f))) + 1;
        final float dx = (-w * graph.range.start) * graph.getScaleRange();
        final float scaleX = graph.getScaleRange();

        final float multi = Range.RANGE_MULTI * 2f;
        final float blockCount = count * multi;
        final float blockW = w / blockCount;
        float stepStart = 4;
        float step = (blockCount - (stepStart * 2)) / (count);
        renderDatesMin(canvas, blockW, scaleX, 0f, dx, (int) blockCount, stepStart, step, dateBound, visibleBound);
        stepStart = stepStart + step /2f;
        renderDatesMin(canvas, blockW, scaleX, 1f, dx, (int) blockCount, stepStart, step, dateBound, visibleBound);
        stepStart = stepStart + step /4f - step;
        step = step / 2f;
        renderDatesMin(canvas, blockW, scaleX, 2f, dx, (int) blockCount, stepStart, step, dateBound, visibleBound);
        renderDatesMin(canvas, blockW, scaleX, 4f, dx, (int) blockCount, stepStart + step / 4f, step / 2f, dateBound, visibleBound);
    }

    private void renderDatesMin(Canvas canvas, float blockW, float scale, float minScale, float dx, int count, float startI, float dI, RectF chartBound, RectF visibleBound) {
        for (float i = startI; i <= count; i = i + dI) {
            float x = chartBound.left + (blockW * i) * scale + dx + blockW / 2;

            final int alpha = Math.round(255f * Math.max(Math.min(scale - minScale, 1f), 0f));
            if (alpha != 0) {
                datePaint.setAlpha(alpha);
                int index = graph.getIndex(x, chartBound);
                float left = x - dateWidth / 2;
                float right = x + dateWidth / 2;
                if ((right < visibleBound.left) || (left > visibleBound.right)) {
                    continue;
                }
                //canvas.drawRect(x - dateWidth / 2, chartBound.top, x + dateWidth /2, chartBound.bottom, linePaint);
                canvas.drawText(graph.getXDate(index), x, chartBound.centerY() + valueHeight / 2f, datePaint);
            }
        }
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