package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.SparseArray;

import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.DateUtils;
import com.telegram.chart.view.utils.ViewUtils;

import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

class XYRender implements Themable {
    private final Graph graph;
    private final TextPaint valuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint datePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint();
    private final SparseArray<String> sparseDates = new SparseArray<>();
    private final SparseArray<String> sparseValues = new SparseArray<>();
    private final float valueHeight;
    private final float dateWidth;
    public int lineColor;
    public int textColor;
    public int backgroundColor;

    public XYRender(Graph data) {
        this.graph = data;
        initPaints();
        valueHeight = measureHeightText(valuePaint);
        dateWidth = datePaint.measureText(DateUtils.XMAX);
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
        lineColor = theme.getAxisColor();
        textColor = theme.getAxisValueColor();
        backgroundColor = theme.getBackgroundWindowColor();
    }

    public void renderYLines(Canvas canvas, RectF r) {
        final long max = graph.state.getMaxChart();
        final float step = calculateStep(0f, max, GRID);
        final float percent = graph.state.progressY();
        if (graph.state.previousStep < graph.state.currentStep) {
            renderYLines(canvas, r, step, 1f - percent, 1f - percent);
            renderYLines(canvas, r, step,  1f / percent, percent);
        } else {
            renderYLines(canvas, r, step, 1f + (percent), 1f - percent);
            renderYLines(canvas, r, step,  percent, percent);
        }
    }

    public void renderYText(Canvas canvas, RectF r) {
        final long max = graph.state.getMaxChart();
        final float step = calculateStep(0f, max, GRID);
        final float percent = graph.state.progressY();
        if (graph.state.previousStep < graph.state.currentStep) {
            renderYText(canvas, r, step, graph.state.previousStep, 1f + (percent), 1f - percent);
            renderYText(canvas, r, step, graph.state.currentStep, percent, percent);
        } else {
            renderYText(canvas, r, step, graph.state.previousStep, 1f - percent, 1f - percent);
            renderYText(canvas, r, step, graph.state.currentStep, 1f / percent, percent);
        }
    }

    public void renderYLines(Canvas canvas, RectF r, float step, float offsetPercentage, float alphaPercentage) {
        final float scaleY = 1f / ((graph.state.getMaxChartStepped() * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        final float offsetY = r.bottom;

        for (int i = 6; i < GRID; i = i + 6) {
            final float y = (-step * i * scaleY) + offsetY;
            if (alphaPercentage != 0f) {
                final int blendColor =  ViewUtils.blendARGB( backgroundColor, lineColor, alphaPercentage);
                linePaint.setColor(blendColor);
                canvas.drawLine(r.left, y, r.right, y, linePaint);
            }
        }
    }

    public void renderYText(Canvas canvas, RectF r, float step, float stepText, float offsetPercentage, float alphaPercentage) {
        final float offsetY = r.bottom;
        final float scaleY = 1f / ((graph.state.getMaxChartStepped() * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        for (int i = 6; i < GRID; i = i + 6) {
            final float y = (-step * i * scaleY) + offsetY;
            final float valueY = y -(valueHeight / 2f) + valuePaint.descent();
            if (alphaPercentage != 0f) {
                int key = i * (int) stepText;
                String value = sparseValues.get(key);
                if (value == null) {
                    value = String.valueOf(key);
                    sparseValues.put(key, value);
                }
                final int blendColor =  ViewUtils.blendARGB( backgroundColor, textColor, alphaPercentage);
                valuePaint.setColor(blendColor);
                canvas.drawText(value, r.left, valueY, valuePaint);
            }
        }
    }

    public void renderY0TextAndLine(Canvas canvas, RectF r) {
        final float text0Y = r.bottom - (valueHeight / 2f) + valuePaint.descent();
        valuePaint.setColor(textColor);
        canvas.drawText(ZERO_Y, r.left, text0Y, valuePaint);
        linePaint.setColor(lineColor);
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

        //magic and need to create better solution
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

            final float alphaPercentage = Math.max(Math.min(scale - minScale, 1f), 0f);
            if (alphaPercentage != 0f) {
                int index = graph.getIndex(x, chartBound);
                float left = x - dateWidth / 2;
                float right = x + dateWidth / 2;
                if ((right < visibleBound.left) || (left > visibleBound.right)) {
                    continue;
                }
                String date = sparseDates.get(index);
                if (date == null) {
                    date = graph.getXDate(index);
                    sparseDates.put(index, date);
                }
                final int blendColor =  ViewUtils.blendARGB( backgroundColor, textColor, alphaPercentage);
                datePaint.setColor(blendColor);
                canvas.drawText(date, x, chartBound.centerY() + valueHeight / 2f, datePaint);
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

    public static final int GRID = 36;
    public static final String ZERO_Y = "0";
}