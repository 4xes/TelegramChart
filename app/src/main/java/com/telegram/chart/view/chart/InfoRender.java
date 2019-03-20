package com.telegram.chart.view.chart;

import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
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
    private final float SPACING_HORIZONTAL = pxFromDp(10f);
    private final float SPACING_VERTICAL = pxFromDp(2f);

    private final Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float RADIUS = pxFromDp(3f);
    private final float OFFSET = pxFromDp(1f);
    private final float BLUR_RADIUS = pxFromDp(2f);
    private final RectF shadowRect = new RectF();
    private final float xLineOffset = pxFromDp(16f);

    private final float dateHeight;
    private final String[] names;
    private final String[] values;
    private final float[] valuesWidth;
    private final float valuesHeight;
    private final float[] namesWidth;
    private final int[] colors;
    private final float namesHeight;

    public InfoRender(Graph graph) {
        this.graph = graph;
        values = new String[graph.size()];
        names = new String[graph.size()];
        valuesWidth = new float[graph.size()];
        namesWidth = new float[graph.size()];
        colors = new int[graph.size()];
        initPaints();

        dateHeight = measureHeightText(paintDate);
        namesHeight = measureHeightText(paintName);
        valuesHeight = measureHeightText(paintValue);
    }

    private void initPaints() {
        paintDate.setStyle(Paint.Style.FILL);
        paintDate.setTextSize(pxFromSp(13f));
        paintDate.setTextAlign(Paint.Align.LEFT);

        paintValue.setStyle(Paint.Style.FILL);
        paintValue.setTextSize(pxFromSp(14f));
        paintValue.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintValue.setTextAlign(Paint.Align.LEFT);

        paintName.setStyle(Paint.Style.FILL);
        paintName.setTextSize(pxFromSp(12f));
        paintName.setTextAlign(Paint.Align.LEFT);

        paintRect.setStyle(Paint.Style.FILL);

        shadowPaint.setColor(Color.BLACK);
        shadowPaint.setStyle(Paint.Style.FILL);
        float SHADOW_DEPTH = 0.9f;
        shadowPaint.setAlpha((int) (100f + 150f * (1f - SHADOW_DEPTH)));
        shadowPaint.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));

    }

    public void render(Canvas canvas, int index, RectF bound, PointF pointF) {
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
                names[n] = graph.getName(id);
                valuesWidth[n] = paintValue.measureText(values[n]);
                namesWidth[n] = paintName.measureText(names[n]);
                colors[n] = graph.getColor(id);
                n++;
            }
        }

        float maxColumnW1 = 0f;
        float maxColumnW2 = 0f;
        for (int i = 0; i < visible; i++) {
            if ((i & 1) == 0) {
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
        final int rows = (visible - 1) / 2;
        final float width = PADDING + Math.max(dateWidth, maxColumnW1 + spacingHorizontal + maxColumnW2) + PADDING;
        final float height = PADDING + dateHeight  + valuesHeight + namesHeight + SPACING_VERTICAL + (rows * (valuesHeight + namesHeight + SPACING_VERTICAL)) + PADDING - paintName.descent();

        float leftRect = pointF.x - xLineOffset;
        if (leftRect < bound.left) {
            leftRect = bound.left;
        }
        if (leftRect + width > bound.right) {
            leftRect = bound.right - width;
        }
        infoRect.set(leftRect, bound.top, leftRect + width, bound.top + height);
        shadowRect.set(infoRect.left + OFFSET, infoRect.top + OFFSET, infoRect.right - OFFSET, infoRect.bottom);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(shadowRect, RADIUS, RADIUS, shadowPaint);
            canvas.drawRoundRect(infoRect, RADIUS, RADIUS, paintRect);
        }

        final float left = infoRect.left + PADDING;
        final float top = infoRect.top + PADDING;

        final float dateY = top + (dateHeight / 2f) + paintDate.descent();
        canvas.drawText(date, left, dateY, paintDate);

        for (int i = 0; i < visible; i++) {
            final int iRow = i / 2;
            final int iColumn = (i & 1) == 0 ? 0 : 1;
            final float x = left + (iColumn * (maxColumnW1 + spacingHorizontal));
            final float offsetValuesY = (valuesHeight / 2f) + paintValue.descent();
            final float yValues = top + dateHeight + offsetValuesY + SPACING_VERTICAL + (iRow * (namesHeight + valuesHeight + SPACING_VERTICAL));
            final float offsetNamesY = (namesHeight / 2f) + paintName.descent();
            final float yNames = yValues - offsetValuesY + valuesHeight + offsetNamesY;
            paintValue.setColor(colors[i]);
            canvas.drawText(values[i], x, yValues, paintValue);
            paintName.setColor(colors[i]);
            canvas.drawText(names[i], x, yNames, paintName);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        paintDate.setColor(theme.getNameColor());
        paintRect.setColor(theme.getBackgroundInfoColor());
    }
}