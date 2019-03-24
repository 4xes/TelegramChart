package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;

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
    private final Paint lineDebug = new Paint();
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

        lineDebug.setStrokeWidth(pxFromDp(1f));
        lineDebug.setStyle(Paint.Style.STROKE);
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
        final float scaleY = 1f / (graph.state.getMaxChartStepped() / r.height());
        final float offsetY = r.bottom;

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
        final float text0Y = offsetY - (valueHeight / 2f) + valuePaint.descent();
        canvas.drawText("0", r.left, text0Y, valuePaint);
        canvas.drawLine(r.left, offsetY, r.right, offsetY, linePaint);
    }

    /*
        final float scaleX = getScaleRange() * sectionWidth(r.width());
        final float scaleY = (1f / (state.chart.yMaxCurrent[id] / r.height())) * state.chart.multiCurrent[id];
        final float dx = (-r.width() * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom;
     */
    public void renderXLines(Canvas canvas, RectF d, RectF c) {
        lineDebug.setColor(Color.BLUE);
        canvas.drawRect(c.left, d.top, c.right, d.bottom, lineDebug);

        final float w = c.width();
//        int count = ((int) (w / 5));
//        int datesCount = count;
//        int allDatesCount = ((datesCount + (datesCount)) * Range.RANGE_MULTI);
//
//        final float sectionW = (w) / (allDatesCount);
//        final float scaleRange = graph.getScaleRange();
//        final float scaleX = scaleRange * sectionW;
//        final float dx = (-c.width() * graph.range.start) * scaleRange;
//        final float offsetX = c.left + dx;
//        final float halfText = dateWidth / 2f;
//
//        for (int i = 1 ;i < allDatesCount; i + co) {
//            lineDebug.setColor(Color.GREEN);
//            float x = i * scaleX + offsetX;
//            canvas.drawText(String.valueOf(i), x + halfText / 2, d.centerY() + datePaint.descent(), datePaint);
//            canvas.drawRect(x, d.top, x, d.bottom, lineDebug);
//        }

        int count = ((int) (w / (dateWidth * 2f))) + 1;

        float datesWidth = (dateWidth * count);
        float newDatesWidth = dateWidth * (count - 1);
        float sum = datesWidth + newDatesWidth;
        float additionalSpaces = (w - sum) / (count - 1);
        float scaleY = graph.getScaleRange();
        float blockWidth = (additionalSpaces + dateWidth + dateWidth);
        final float dx = (-w * graph.range.start) * scaleY;

        for (int i = 0; i < count; i++) {
            float left = c.left + ((blockWidth) * i * scaleY) + dx;
            float right = left + dateWidth;
            lineDebug.setColor(Color.GREEN);
            renderXLines(canvas, d, (int) (left + datesWidth / 2f));
            //canvas.drawRect(left, d.top, right, d.bottom, lineDebug);
        }
    }

    public void renderXLines(Canvas canvas, RectF r, int i) {
        int w = (int)r.width();
        int count = w;
        float scaleRange = graph.getScaleRange();
        float sectionWidth = 1;
        final float dx = (-w * graph.range.start) * scaleRange;
        final float offsetX = r.left + dx;
        //canvas.drawRect(left, d.top, right, d.bottom, lineDebug);
        /*
        final float width = r.width();
        final float scaleRange = getScaleRange();
        final float scaleX = scaleRange * sectionWidth(width);
        final float dx = (-width * range.start) * scaleRange;
        final float offsetX = r.left + dx;
        final float scaleY = 1f / (state.chart.yMaxCurrent[id] / r.height());
        final float offsetY = r.bottom;
         */
    }


    public void renderVLine(Canvas canvas, RectF r, float x) {
        canvas.drawLine(x, r.top, x, r.bottom, linePaint);
    }

    public void renderXTempLines(Canvas canvas, RectF d, RectF c) {
        final float w = c.width();
        int count = ((int) (w / (dateWidth * 2f))) + 1;

        float datesWidth = (dateWidth * count);
        float newDatesWidth = dateWidth * (count - 1);
        float sum = datesWidth + newDatesWidth;
        float additionalSpaces = (w - sum) / (count - 1);
        float blockWidth = (additionalSpaces + dateWidth + dateWidth);

        for (int i = 0; i < count; i++) {
            float left = c.left + ((blockWidth) * i);
            float x = left + dateWidth / 2f;

            int index = graph.getIndex(x, c);
            canvas.drawText(graph.getXDate(index), x, d.centerY() + valueHeight / 2f, datePaint);
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