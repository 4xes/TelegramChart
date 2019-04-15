package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.ViewUtils;

import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

abstract class BasePieRender extends Render {
    private final PointF point = new PointF();
    private float sumLines = 0;
    private final float sum[];
    private RectF rectPie = new RectF();
    private RectF rectPieSelected = new RectF();
    private float padding = ViewUtils.pxFromDp(8f);
    private final TextPaint paintPercent = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public BasePieRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);
        final int count = manager.countLines();
        sum = new float[count];
        initPaints();
    }

    protected void initPaints() {
        paintPercent.setStyle(Paint.Style.FILL);
        paintPercent.setTextSize(pxFromSp(11f));
        paintPercent.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintPercent.setTextAlign(Paint.Align.CENTER);

        for (int id = 0; id < manager.countLines(); id++) {
            paint[id].setAntiAlias(true);
            paint[id].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        super.applyTheme(theme);
        for (int id = 0; id < manager.countLines(); id++) {
            if (theme.id == Theme.DAY) {
                paint[id].setColor(manager.chart.data[id].color);
            } else {
                paint[id].setColor(manager.chart.data[id].colorNight);
            }
        }
        paintPercent.setColor(theme.titleColor);
    }

    public void recalculateBars(RectF chart, int lower, int upper) {
        final float height = chart.height();
        for (int id = 0; id < manager.countLines(); id++) {
            sum[id] = 0;
        }
        for (int i = lower; i <= upper; i++) {
            float sumPoint = 0;

            for (int id = 0; id < manager.countLines(); id++) {
                sumPoint += manager.chart.data[id].y[i] * manager.state.chart.percentCurrent[id];
            }
            for (int id = 0; id < manager.countLines(); id++) {
                sum[id] += Math.ceil((manager.chart.data[id].y[i] * manager.state.chart.percentCurrent[id]) / (sumPoint/ height));
            }
        }

        sumLines = 0;
        for (int id = 0; id < manager.countLines(); id++) {
            sumLines += sum[id];
        }
    }

    protected abstract void updateMatrix(RectF chart);

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible, int selectIndex) {
        rectPie.set(chart);
        rectPie.left = rectPie.left + (chart.width() - chart.height()) / 2;
        rectPie.right = rectPie.left + chart.height();
        rectPie.inset(-padding, -padding);
        rectPieSelected.set(rectPie);
        rectPieSelected.inset(-padding, -padding);
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        recalculateBars(chart, lower, upper);
        float startAngle = 0f;
        for (int id = manager.countLines() - 1; id >= 0; id--) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);

            if (alpha != 0) {
                paint[id].setAlpha(alpha);
                float endAngle = 360f * (sum[id] / sumLines);
                canvas.drawArc(rectPie, startAngle, endAngle, true, paint[id]);
                startAngle += endAngle;

            }
        }
        startAngle = 0f;
        for (int id = manager.countLines() - 1; id >= 0; id--) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);

            if (alpha != 0) {
                float endAngle = 360f * (sum[id] / sumLines);
                float medianAngle = (startAngle + (endAngle / 2f)) * (float)Math.PI / 180f;
                float radius = rectPie.width() /4f;
                float x = (float)(rectPie.centerX() + (radius * Math.cos(medianAngle)));
                float y = (float)(rectPie.centerY() + (radius * Math.sin(medianAngle)));

                int percent = (int) Math.round((((double) (sum[id])) / sumLines) * 100f);
                paintPercent.setAlpha(alpha);
                canvas.drawText(percent + "%", x, y, paintPercent);
                startAngle += endAngle;

            }
        }
    }
}