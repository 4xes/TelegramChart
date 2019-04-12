package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.telegram.chart.R;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.DateUtils;

import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class TooltipRender implements Themable {

    private GraphManager graphManager;
    private final Paint paintRect = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintDate = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintValue = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintName = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF infoRect = new RectF();
    private final float PADDING = pxFromDp(8f);
    private final float SPACING_HORIZONTAL = pxFromDp(10f);
    private final float SPACING_VERTICAL = pxFromDp(2f);

    private final Paint paintShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float OFFSET = pxFromDp(1f);
    private final float BLUR_RADIUS = pxFromDp(2f);
    private final RectF shadowRect = new RectF();
    private final float xLineOffset = pxFromDp(16f);
    private final Drawable shadowDrawableDay;
    private final Drawable shadowDrawableNight;
    private boolean isDay = true;
    private final float dateHeight;
    private final String[] names;
    private final String[] values;
    private final float[] valuesWidth;
    private final float valuesHeight;
    private final float[] namesWidth;
    private final int[] colors;
    private final float namesHeight;

    public TooltipRender(GraphManager graphManager, Context context) {
        this.graphManager = graphManager;
        values = new String[graphManager.countLines()];
        names = new String[graphManager.countLines()];
        valuesWidth = new float[graphManager.countLines()];
        namesWidth = new float[graphManager.countLines()];
        colors = new int[graphManager.countLines()];
        initPaints();

        dateHeight = measureHeightText(paintDate);
        namesHeight = measureHeightText(paintName);
        valuesHeight = measureHeightText(paintValue);

        shadowDrawableDay = context.getResources().getDrawable(R.drawable.shadow_day);
        shadowDrawableNight = context.getResources().getDrawable(R.drawable.shadow_night);
    }

    private void initPaints() {
        paintDate.setStyle(Paint.Style.FILL);
        paintDate.setTextSize(pxFromSp(12f));
        paintDate.setTextAlign(Paint.Align.LEFT);

        paintValue.setStyle(Paint.Style.FILL);
        paintValue.setTextSize(pxFromSp(14f));
        paintValue.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintValue.setTextAlign(Paint.Align.LEFT);

        paintName.setStyle(Paint.Style.FILL);
        paintName.setTextSize(pxFromSp(11f));
        paintName.setTextAlign(Paint.Align.LEFT);

        paintRect.setStyle(Paint.Style.FILL);

        paintShadow.setColor(Color.BLACK);
        paintShadow.setStyle(Paint.Style.FILL);
        float SHADOW_DEPTH = 0.9f;
        paintShadow.setAlpha((int) (100f + 150f * (1f - SHADOW_DEPTH)));
        paintShadow.setMaskFilter(new BlurMaskFilter(BLUR_RADIUS, BlurMaskFilter.Blur.NORMAL));
    }

    public void render(Canvas canvas, int index, RectF bound, PointF pointF) {
        int visible = graphManager.countVisible();
        if (visible == 0) {
            return;
        }
        final String date = DateUtils.getInfoDate(graphManager.chart.x[index] * 1000L);
        final float dateWidth = paintDate.measureText(date);

        int n = 0;
        for (int id = 0; id < graphManager.countLines(); id++) {
            if (graphManager.state.visible[id]) {
                values[n] = String.valueOf(graphManager.chart.data[id].y[index]);
                names[n] = graphManager.chart.data[id].name;
                valuesWidth[n] = paintValue.measureText(values[n]);
                namesWidth[n] = paintName.measureText(names[n]);
                colors[n] = graphManager.chart.data[id].color;
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

        if (isDay) {
            shadowDrawableDay.setBounds((int) shadowRect.left, (int) shadowRect.top, (int) shadowRect.right, (int) shadowRect.bottom);
            shadowDrawableDay.draw(canvas);
        } else {
            shadowDrawableNight.setBounds((int) shadowRect.left, (int) shadowRect.top, (int) shadowRect.right, (int) shadowRect.bottom);
            shadowDrawableNight.draw(canvas);
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
        paintDate.setColor(theme.nameColor);
        paintRect.setColor(theme.tooltipColor);
        isDay = theme.getId() == Theme.DAY;
    }
}