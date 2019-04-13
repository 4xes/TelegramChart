package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.SparseArray;

import com.telegram.chart.data.Chart;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.DateUtils;

import static com.telegram.chart.data.Chart.toStepped;
import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class XYRender implements Themable {
    private final GraphManager manager;
    private final TextPaint yPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint xPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint();
    private final SparseArray<String> sparseDates = new SparseArray<>();
    private final SparseArray<String> sparseValues = new SparseArray<>();
    private final float valueHeight;
    private final float dateWidth;
    private int yAxisAlpha;
    private int xAxisAlpha;
    private int lineAlpha;

    public XYRender(GraphManager manager) {
        this.manager = manager;
        initPaints();
        valueHeight = measureHeightText(yPaint);
        dateWidth = xPaint.measureText(DateUtils.X_FORMAT_MAX);
    }

    private void initPaints() {
        yPaint.setStyle(Paint.Style.FILL);
        yPaint.setTextSize(pxFromSp(11f));
        yPaint.setTextAlign(Paint.Align.LEFT);

        xPaint.setStyle(Paint.Style.FILL);
        xPaint.setTextSize(pxFromSp(11f));
        xPaint.setTextAlign(Paint.Align.CENTER);

        linePaint.setStrokeWidth(pxFromDp(1f));
        linePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void applyTheme(Theme theme) {
        lineAlpha = Color.alpha(theme.gridColor);
        linePaint.setColor(theme.gridColor);
        if (manager.chart.type.equals(Chart.TYPE_BAR_STACKED) || manager.chart.type.equals(Chart.TYPE_PERCENTAGE)) {
            xAxisAlpha = Color.alpha(theme.axisStackedX);
            yAxisAlpha = Color.alpha(theme.axisStackedY);
            xPaint.setColor(theme.axisStackedX);
            yPaint.setColor(theme.axisStackedY);
        } else {
            xAxisAlpha = Color.alpha(theme.axisX);
            yAxisAlpha = Color.alpha(theme.axisY);
            xPaint.setColor(theme.axisX);
            yPaint.setColor(theme.axisY);
        }
    }

    public void renderYLines(Canvas canvas, RectF r) {
        final int max = manager.state.maxCurrent;
        final int maxStepped = toStepped(max);
        final float step = calculateStep(0f, max, GRID);
        final float percent = manager.state.progressAxis();
        if (manager.state.previousStep < manager.state.currentStep) {
            renderYLines(canvas, r, step, 1f + (percent), 1f - percent, maxStepped);
            renderYLines(canvas, r, step,  percent, percent, maxStepped);
        } else {
            renderYLines(canvas, r, step, 1f - percent, 1f - percent, maxStepped);
            renderYLines(canvas, r, step, 1f / percent, percent, maxStepped);
        }
    }

    public void renderYText(Canvas canvas, RectF r) {
        final int max = manager.state.maxCurrent;
        final int maxStepped = toStepped(max);
        final float step = calculateStep(0f, max, GRID);
        final float percent = manager.state.progressAxis();
        if (manager.state.previousStep < manager.state.currentStep) {
            renderYText(canvas, r, step, manager.state.previousStep, 1f + (percent), 1f - percent, maxStepped);
            renderYText(canvas, r, step, manager.state.currentStep, percent, percent, maxStepped);
        } else {
            renderYText(canvas, r, step, manager.state.previousStep, 1f - percent, 1f - percent, maxStepped);
            renderYText(canvas, r, step, manager.state.currentStep, 1f / percent, percent, maxStepped);
        }
    }

    public void renderYLines(Canvas canvas, RectF r, float step, float offsetPercentage, float alphaPercentage, int maxStepped) {
        final float scaleY = 1f / ((maxStepped * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        final float offsetY = r.bottom;

        for (int i = 6; i < GRID; i = i + 6) {
            final float y = (float) Math.ceil((-step * i * scaleY) + offsetY);
            int alpha = (int) Math.ceil(lineAlpha * alphaPercentage);
            if (alpha != 0) {
                linePaint.setAlpha(alpha);
                canvas.drawLine(r.left, y, r.right, y, linePaint);
            }
        }
    }

    public void renderYText(Canvas canvas, RectF r, float step, float stepText, float offsetPercentage, float alphaPercentage, int maxStepped) {
        final float offsetY = r.bottom;
        final float scaleY = 1f / ((maxStepped * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        for (int i = 6; i < GRID; i = i + 6) {
            final float y = (-step * i * scaleY) + offsetY;
            final float valueY = y -(valueHeight / 2f) + yPaint.descent();
            int alpha = (int) Math.ceil(yAxisAlpha * alphaPercentage);
            if (alpha != 0) {
                int key = i * (int) stepText;
                String value = sparseValues.get(key);
                if (value == null) {
                    value = String.valueOf(key);
                    sparseValues.put(key, value);
                }
                yPaint.setAlpha(alpha);
                canvas.drawText(value, r.left, valueY, yPaint);
            }
        }
    }

    public void renderY0TextAndLine(Canvas canvas, RectF r) {
        final float text0Y = r.bottom - (valueHeight / 2f) + yPaint.descent();
        yPaint.setAlpha(yAxisAlpha);
        canvas.drawText(ZERO_Y, r.left, text0Y, yPaint);
        linePaint.setAlpha(lineAlpha);
        canvas.drawLine(r.left, r.bottom, r.right, r.bottom, linePaint);
    }

    public void renderVLine(Canvas canvas, RectF r, float x) {
        canvas.drawLine(x, r.top, x, r.bottom, linePaint);
    }

    public void renderXLines(Canvas canvas, RectF dateBound, RectF chartBound, RectF visibleBound) {
        final float w = chartBound.width();
        int count = ((int) (w / (dateWidth * 2f))) + 1;
        final float dx = (-w * manager.range.start) * manager.getScaleRange();
        final float scaleX = manager.getScaleRange();

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
            int alpha = (int) Math.ceil(xAxisAlpha * alphaPercentage);
            if (alpha != 0) {
                int index = manager.getIndex(x, chartBound);
                float left = x - dateWidth / 2;
                float right = x + dateWidth / 2;
                if ((right < visibleBound.left) || (left > visibleBound.right)) {
                    continue;
                }
                String date = sparseDates.get(index);
                if (date == null) {
                    date = DateUtils.getDateX(manager.chart.x[index] * 1000L);
                    sparseDates.put(index, date);
                }
                xPaint.setAlpha(alpha);
                canvas.drawText(date, x, chartBound.centerY() + valueHeight / 2f, xPaint);
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