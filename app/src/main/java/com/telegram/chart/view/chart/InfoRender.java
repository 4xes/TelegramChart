package com.telegram.chart.view.chart;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.utils.DateUtils;

import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class InfoRender implements Themable<Theme> {

    private Graph graph;
    private final Paint paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintValue = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintName = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF infoRect = new RectF();
    private final float PADDING = pxFromDp(8f);
    private final float SPACING_HORIZONTAL = pxFromDp(12f);
    private final float SPACING_VERTICAL = pxFromDp(8f);

    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float RADIUS = pxFromDp(3f);
    private final float BLUR_RADIUS = pxFromDp(2f);
    private final RectF shadowRect = new RectF();

    private final float dateHeight;
    private final String[] values;
    private final float[] valuesWidth;
    private final float valuesHeight;
    private final float[] namesWidth;
    private final float namesHeight;

    public InfoRender(Graph graph) {
        this.graph = graph;
        values = new String[graph.size()];
        valuesWidth = new float[graph.size()];
        namesWidth = new float[graph.size()];
        initPaints();

        dateHeight = measureHeightText(paintDate);
        namesHeight = measureHeightText(paintName);
        valuesHeight = measureHeightText(paintValue);
    }

    private void initPaints() {
        paintDate.setStyle(Paint.Style.FILL);
        paintDate.setTextSize(pxFromSp(12f));
        paintDate.setTextAlign(Paint.Align.CENTER);
        paintValue.setStyle(Paint.Style.FILL);
        paintName.setStyle(Paint.Style.FILL);

        paintRect.setStyle(Paint.Style.FILL);

        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStyle(Paint.Style.FILL);
        float SHADOW_DEPTH = 0.9f;
        shadowPaint.setAlpha((int) (100f + 150f * (1f - SHADOW_DEPTH)));
        shadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));

    }

    public void render(Canvas canvas, int index, Bound bound, PointF pointF) {
        int visible = graph.countVisible();
        if (visible == 0) {
            return;
        }
        final String date = DateUtils.getInfoDate(index);
        final float dateWidth = paintDate.measureText(date);

        int n = 0;
        for (int id = 0; id < graph.size(); id++) {
            if (graph.isVisible(id)) {
                values[n] = graph.getValue(id, index);
                valuesWidth[n] = paintValue.measureText(values[n]);
                namesWidth[n] = paintName.measureText(graph.getName(id));
                n++;
            }
        }

        float maxColumnW1 = 0f;
        float maxColumnW2 = 0f;
        for (int i = 0; i < visible; i++) {
            if ((n & 1) == 0) {
                if (maxColumnW1 < valuesWidth[i]) {
                    maxColumnW1 = valuesWidth[i];
                }
                if (maxColumnW1 < namesWidth[i]) {
                    maxColumnW1 = namesWidth[i];
                }
            } else {
                if (maxColumnW2 < valuesWidth[i]) {
                    maxColumnW2 = valuesWidth[i];
                }
                if (maxColumnW2 < namesWidth[i]) {
                    maxColumnW2 = namesWidth[i];
                }
            }
        }

        final float spacingHorizontal = visible > 1 ? SPACING_HORIZONTAL : 0;
        final int rows = (visible % 2) + visible / 2;
        final float width = PADDING + Math.max(dateWidth, maxColumnW1 + spacingHorizontal + maxColumnW2) + PADDING;
        final float height = PADDING + dateHeight  + ((SPACING_VERTICAL + valuesHeight + namesHeight) * rows) + PADDING;

        infoRect.set(bound);
        infoRect.left = infoRect.left + width;
        infoRect.bottom = infoRect.top + height;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(infoRect, RADIUS, RADIUS, shadowPaint);
            canvas.drawRoundRect(infoRect, RADIUS, RADIUS, paintRect);

            final float dateX = infoRect.left + PADDING + (dateWidth / 2f);
            final float dateY = infoRect.top + PADDING + (dateHeight / 2f) + paintDate.descent();

            canvas.drawText(date, dateX, dateY, paintDate);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        paintDate.setColor(theme.getNameColor());
        paintRect.setColor(theme.getBackgroundInfoColor());
    }
}