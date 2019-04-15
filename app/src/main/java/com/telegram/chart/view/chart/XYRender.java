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
import com.telegram.chart.view.utils.AxisUtils;
import com.telegram.chart.view.utils.DateUtils;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class XYRender implements Themable {
    private final GraphManager manager;
    private final TextPaint yPaintLeft = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint yPaintRight = new TextPaint(Paint.ANTI_ALIAS_FLAG);
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
        valueHeight = measureHeightText(yPaintLeft);
        dateWidth = xPaint.measureText(DateUtils.X_FORMAT_MAX);
    }

    private void initPaints() {
        yPaintLeft.setStyle(Paint.Style.FILL);
        yPaintLeft.setTextSize(pxFromSp(11f));
        yPaintLeft.setTextAlign(Paint.Align.LEFT);

        yPaintRight.setStyle(Paint.Style.FILL);
        yPaintRight.setTextSize(pxFromSp(11f));
        yPaintRight.setTextAlign(Paint.Align.RIGHT);

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
        if (manager.chart.isStacked || manager.chart.isPercentage) {
            xAxisAlpha = Color.alpha(theme.axisStackedX);
            yAxisAlpha = Color.alpha(theme.axisStackedY);
            xPaint.setColor(theme.axisStackedX);
            yPaintLeft.setColor(theme.axisStackedY);
        } else {
            xAxisAlpha = Color.alpha(theme.axisX);
            yAxisAlpha = Color.alpha(theme.axisY);
            xPaint.setColor(theme.axisX);
            yPaintLeft.setColor(theme.axisY);
        }
        if (manager.chart.isScaled) {
            yAxisAlpha = 255;
            if (theme.id == Theme.DAY) {
                yPaintLeft.setColor(manager.chart.data[0].color);
                yPaintRight.setColor(manager.chart.data[1].color);
            } else {
                yPaintLeft.setColor(manager.chart.data[0].colorNight);
                yPaintRight.setColor(manager.chart.data[1].colorNight);
            }
        }
    }

    public void renderYLines(Canvas canvas, RectF r) {
        if (manager.chart.isPercentage) {
            renderYPercentageLines(canvas, r, r.height() / 4);
            renderYPercentageTextLeft(canvas, r, 25f, r.height() / 4);
            return;
        }
        final int max = manager.state.maxCurrent;
        final int min = manager.state.minCurrent;
        final int maxStepped = maxStepped(max);
        float step = calculateStep(0, max, GRID);
        final int minStepped = Chart.minStepped(min, step);
        step = (maxStepped - minStepped) / (float) GRID;

        final float percent = manager.state.progressAxis();

        final int scaleMax = maxStepped - minStepped;

        if (manager.state.previousMaxChart < manager.state.currentMaxChart) {
            renderYLines(canvas, r, step, 1f + (percent), 1f - percent, scaleMax);
            renderYLines(canvas, r, step,  percent, percent, scaleMax);
            renderYTextLeft(canvas, r, step, manager.state.previousStep, manager.state.previousMinChart, 1f + (percent), 1f - percent, scaleMax);
            renderYTextLeft(canvas, r, step, step, minStepped, percent, percent, scaleMax);
        } else {
            renderYLines(canvas, r, step, 1f - percent, 1f - percent, scaleMax);
            renderYLines(canvas, r, step, 1f / percent, percent, scaleMax);
            renderYTextLeft(canvas, r, step, manager.state.previousStep, manager.state.previousMinChart,1f - percent, 1f - percent, scaleMax);
            renderYTextLeft(canvas, r, step, step, minStepped, 1f / percent, percent, scaleMax);
        }

        final int max2 = manager.state.maxCurrent2;
        final int min2 = manager.state.minCurrent2;
        final int maxStepped2 = maxStepped(max2);
        float step2 = calculateStep(0, max2, GRID);
        final int minStepped2 = Chart.minStepped(min2, step2);
        step2 = (maxStepped2 - minStepped2) / (float) GRID;

        final float percent2 = manager.state.progressAxis2();

        final int scaleMax2 = maxStepped2 - minStepped2;
        if (manager.chart.isScaled) {
            if (manager.state.previousMaxChart2 < manager.state.currentMaxChart2) {
                renderYTextRight(canvas, r, step2, manager.state.previousStep2, manager.state.previousMinChart2, 1f + (percent2), 1f - percent2, scaleMax2);
                renderYTextRight(canvas, r, step2, step2, minStepped2, percent2, percent2, scaleMax2);
            } else {
                renderYTextRight(canvas, r, step2, manager.state.previousStep2, manager.state.previousMinChart2,1f - percent2, 1f - percent2, scaleMax2);
                renderYTextRight(canvas, r, step2, step2, minStepped2, 1f / percent2, percent2, scaleMax2);
            }
        }

        renderMinTextAndLine(canvas, r, minStepped);
    }

    public void renderYLines(Canvas canvas, RectF r, float step, float offsetPercentage, float alphaPercentage, int scaleMax) {
        final float scaleY = 1f / ((scaleMax * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        final float offsetY = r.bottom;

        for (int i = STEP; i < GRID; i = i + STEP) {
            final float y = (float) Math.ceil((-step * i * scaleY) + offsetY);
            int alpha = (int) Math.ceil(lineAlpha * alphaPercentage);
            if (alpha != 0) {
                linePaint.setAlpha(alpha);
                canvas.drawLine(r.left, y, r.right, y, linePaint);
            }
        }
    }

    public void renderYTextLeft(Canvas canvas, RectF r, float step, float stepText, int minText, float offsetPercentage, float alphaPercentage, int scaleMax) {
        final float offsetY = r.bottom;
        final float scaleY = 1f / ((scaleMax * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        final int start = manager.chart.isLined ? 0 : STEP;
        for (int i = start; i < GRID; i = i + STEP) {
            final float y = (-step * i * scaleY) + offsetY;
            final float valueY = y -(valueHeight / 2f) + yPaintLeft.descent();
            int alpha = (int) Math.ceil(yAxisAlpha * alphaPercentage);
            if (alpha != 0) {
                int key = i * (int) stepText + minText;
                String value = AxisUtils.formatAxis(key, !manager.chart.isLined);
                yPaintLeft.setAlpha(alpha);
                canvas.drawText(value, r.left, valueY, yPaintLeft);
            }
        }
    }

    public void renderYPercentageLines(Canvas canvas, RectF r, float step) {
        final float offsetY = r.bottom;

        for (int i = 0; i < 5; i++) {
            final float y = -step * i + offsetY;
            canvas.drawLine(r.left, y, r.right, y, linePaint);
        }
    }

    public void renderYPercentageTextLeft(Canvas canvas, RectF r, float percent, float step) {
        final float offsetY = r.bottom;

        for (int i = 0; i < 5; i++) {
            final float y = -step * i + offsetY;
            final float valueY = y -(valueHeight / 2f) + yPaintLeft.descent();
            int key = i * (int) percent;
            String value = sparseValues.get(key);
            if (value == null) {
                value = String.valueOf(key);
                sparseValues.put(key, value);
            }
            canvas.drawText(value, r.left, valueY, yPaintLeft);
        }
    }

    public void renderYTextRight(Canvas canvas, RectF r, float step, float stepText, int minText, float offsetPercentage, float alphaPercentage, int scaleMax) {
        final float offsetY = r.bottom;
        final float scaleY = 1f / ((scaleMax * Math.max(offsetPercentage, 0.0000001f)) / r.height());
        final int start = manager.chart.isLined ? 0 : STEP;
        for (int i = start; i < GRID; i = i + STEP) {
            final float y = (-step * i * scaleY) + offsetY;
            final float valueY = y -(valueHeight / 2f) + yPaintLeft.descent();
            int alpha = (int) Math.ceil(yAxisAlpha * alphaPercentage);
            if (alpha != 0) {
                int key = i * (int) stepText + minText;
                String value = AxisUtils.formatAxis(key, !manager.chart.isLined);
                yPaintRight.setAlpha(alpha);
                canvas.drawText(value, r.right, valueY, yPaintRight);
            }
        }
    }

    public void renderMinTextAndLine(Canvas canvas, RectF r, float min) {
        if (!manager.chart.isLined) {
            final float text0Y = r.bottom - (valueHeight / 2f) + yPaintLeft.descent();
            yPaintLeft.setAlpha(yAxisAlpha);
            int key = (int) min;
            String value = sparseValues.get(key);
            if (value == null) {
                value = String.valueOf(key);
                sparseValues.put(key, value);
            }
            canvas.drawText(value, r.left, text0Y, yPaintLeft);
        }
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

    public static final int GRID = 100;
    public static final int STEP = 15;
}