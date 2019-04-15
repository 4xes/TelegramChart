package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.SparseArray;

import com.telegram.chart.R;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.DateUtils;

import static com.telegram.chart.view.utils.ViewUtils.measureHeightText;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;

public class TooltipRender implements Themable {

    private GraphManager manager;
    private final TextPaint paintDate = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint paintValue = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint paintName = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private final TextPaint paintPercent = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint paintArrow = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF infoRect = new RectF();
    private final float PADDING = pxFromDp(16f);
    private final float SPACING_HORIZONTAL = pxFromDp(10f);
    private final float SPACING_VERTICAL = pxFromDp(4f);
    private final float cursorOffset = pxFromDp(8f);
    private final float arrowWidth = pxFromDp(4f);
    private final float offsetArrow = pxFromDp(2f);
    private final float arrowHeight = pxFromDp(8f);
    private final float arrowPadding = pxFromDp(20f);
    private final Drawable shadowDrawableDay;
    private final Drawable shadowDrawableNight;
    private boolean isDay = true;
    private final SparseArray<String> sparsePercent = new SparseArray<>();
    private final SparseArray<String> sparseYears = new SparseArray<>();
    private final SparseArray<String> sparseValues = new SparseArray<>();
    private final float valuesHeight;
    private final float namesHeight;
    private final float dateHeight;
    private final float maxValueWidth;
    private final float maxDateWidth;
    private final float maxDateDayWidth;
    private final float maxNameWidth;
    private final float maxPercentWidth;
    private float arrowWidthStroke = pxFromDp(1.5f);
    private int sumColor;
    float width = 0;
    float height = 0;
    private float[] arrowPoints = new float[8];

    public TooltipRender(GraphManager manager, Context context) {
        this.manager = manager;
        initPaints();

        shadowDrawableDay = context.getResources().getDrawable(R.drawable.shadow_day);
        shadowDrawableNight = context.getResources().getDrawable(R.drawable.shadow_night);

        dateHeight = measureHeightText(paintDate);
        namesHeight = measureHeightText(paintName);
        valuesHeight = measureHeightText(paintValue);
        maxDateDayWidth = paintDate.measureText(DateUtils.TOOLTIP_DAY_FORMAT_MAX);
        maxDateWidth = maxDateDayWidth + paintDate.measureText(DateUtils.TOOLTIP_YEAR_FORMAT_MAX);
        maxValueWidth = paintValue.measureText(String.valueOf(manager.chart.max));
        maxNameWidth = paintValue.measureText(manager.chart.maxLengthName);
        maxPercentWidth = paintPercent.measureText("100% ");
    }

    public String getPercent(int percent) {
        String valueString = sparsePercent.get(percent);
        if (valueString == null) {
            valueString = percent + "% ";
            sparseYears.put(percent, valueString);
        }
        return valueString;
    }

    public String getValue(int value) {
        String valueString = sparseValues.get(value);
        if (valueString == null) {
            valueString = String.valueOf(value);
            sparseYears.put(value, valueString);
        }
        return valueString;
    }

    public String getDate(int index) {
        String valueString = sparseValues.get(index);
        if (valueString == null) {
            valueString = DateUtils.getToolTipDay(manager.chart.x[index] * 1000L) + DateUtils.getToolTipMonthAndYear(manager.chart.x[index] * 1000L);
            sparseYears.put(index, valueString);
        }
        return valueString;
    }

    private void initPaints() {
        paintDate.setStyle(Paint.Style.FILL);
        paintDate.setTextSize(pxFromSp(12f));
        paintDate.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintDate.setTextAlign(Paint.Align.LEFT);

        paintValue.setStyle(Paint.Style.FILL);
        paintValue.setTextSize(pxFromSp(11f));
        paintValue.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintValue.setTextAlign(Paint.Align.RIGHT);

        paintPercent.setStyle(Paint.Style.FILL);
        paintPercent.setTextSize(pxFromSp(11f));
        paintPercent.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paintPercent.setTextAlign(Paint.Align.RIGHT);

        paintName.setStyle(Paint.Style.FILL);
        paintName.setTextSize(pxFromSp(11f));
        paintName.setTextAlign(Paint.Align.LEFT);


        paintArrow.setStrokeWidth(arrowWidthStroke);
        paintArrow.setColor(Color.WHITE);
        paintArrow.setStyle(Paint.Style.STROKE);
        paintArrow.setStrokeCap(Paint.Cap.ROUND);
    }

    public void measure(RectF bound) {
        int visible = manager.countVisible();
        final boolean hasPercent = manager.chart.isPercentage;
        final float percentMaxWidth = hasPercent ? maxPercentWidth: 0;
        final boolean hasSum = (manager.chart.isStacked) && visible > 1;
        final float percentageSumHeight = hasSum ? namesHeight + SPACING_VERTICAL: 0;
        final float titleMaxWidth = maxDateWidth + arrowPadding + arrowWidth;
        final float rowMaxWidth = percentMaxWidth + maxNameWidth + SPACING_HORIZONTAL + maxValueWidth;
        width = PADDING + Math.max(titleMaxWidth, rowMaxWidth) + PADDING;
        height = PADDING + dateHeight + SPACING_VERTICAL + (visible * (namesHeight + SPACING_VERTICAL)) + percentageSumHeight + PADDING - paintName.descent();
        infoRect.top = bound.top;
        infoRect.bottom = bound.top + height;

        arrowPoints[0] = infoRect.right - PADDING - arrowWidth - offsetArrow;
        arrowPoints[1] = infoRect.top + PADDING + offsetArrow;
        arrowPoints[2] = arrowPoints[0] + arrowWidth;
        arrowPoints[3] = arrowPoints[1] + (arrowHeight / 2f);
        arrowPoints[4] = arrowPoints[2];
        arrowPoints[5] = arrowPoints[3];
        arrowPoints[6] = arrowPoints[0];
        arrowPoints[7] = arrowPoints[1] + arrowHeight;
    }

    public float getDrawPosition(RectF bound, PointF point) {
        float x = 0;
        float tooltipPositionBottom = bound.top + height + cursorOffset * 2f;
        float topPosition = (bound.bottom - point.y);

        if (manager.chart.isPercentage) {
            if (x + width + cursorOffset > bound.right) {
                x = bound.right - cursorOffset - width;
            } else {
                x = point.x + (cursorOffset);
                if (x + width + cursorOffset > bound.right) {
                    x = x - width - (cursorOffset);
                }
            }
            return x;
        }

        if (tooltipPositionBottom > topPosition) {
            x = point.x - cursorOffset * 2f;

            if (x + width + cursorOffset > bound.right) {
                x = bound.right - cursorOffset - width;
            }
        } else {
            x = point.x + (cursorOffset);
            if (x + width + cursorOffset > bound.right) {
                x = x - width - (cursorOffset);
            }
        }
        return x;
    }

    public void setPosition(float x) {
        infoRect.left = x;
        infoRect.right = x + width;
    }

    public int getPosition() {
        return (int) infoRect.left;
    }

    public void render(Canvas canvas, int index) {
        int visible = manager.countVisible();
        if (visible == 0) {
            return;
        }

        final boolean hasPercent = manager.chart.isPercentage;
        final float percentMaxWidth = hasPercent ? maxPercentWidth: 0;
        final boolean hasSum = (manager.chart.isStacked) && visible > 1;

        if (isDay) {
            shadowDrawableDay.setBounds((int) infoRect.left, (int) infoRect.top, (int) infoRect.right, (int) infoRect.bottom);
            shadowDrawableDay.draw(canvas);
        } else {
            shadowDrawableNight.setBounds((int) infoRect.left, (int) infoRect.top, (int) infoRect.right, (int) infoRect.bottom);
            shadowDrawableNight.draw(canvas);
        }

        final float left = infoRect.left + PADDING;
        final float right = infoRect.right - PADDING;
        final float top = infoRect.top + PADDING;

        final float dateY = top + (dateHeight / 2f) + paintDate.descent();
        canvas.drawText(getDate(index), left, dateY, paintDate);

        float y = top + namesHeight + SPACING_VERTICAL;
        int sum = 0;
        for (int id = 0; id < manager.countLines(); id++) {
            if (manager.chart.visible[id]) {
                sum += manager.chart.data[id].y[index];
            }
        }
        final float valueOffsetY = (valuesHeight / 2f) + paintValue.descent();
        for (int id = 0; id < manager.countLines(); id++) {
            if (manager.chart.visible[id]) {
                int value = manager.chart.data[id].y[index];
                if (hasPercent) {
                    int percent = (int) Math.round((((double) (value)) / sum) * 100f);
                    canvas.drawText(getPercent(percent), left + percentMaxWidth, y + valueOffsetY, paintPercent);
                }
                canvas.drawText(manager.chart.data[id].name, left + (hasPercent? percentMaxWidth: 0), y + valueOffsetY, paintName);
                paintValue.setColor(isDay? manager.chart.data[id].tooltipColor: manager.chart.data[id].tooltipColorNight);
                canvas.drawText(getValue(value), right, y + valueOffsetY, paintValue);
                y += namesHeight + SPACING_VERTICAL;
            }
        }
        if (hasSum) {
            canvas.drawText("All", left, y + valueOffsetY, paintName);
            paintValue.setColor(sumColor);
            canvas.drawText(getValue(sum), right, y + valueOffsetY, paintValue);
        }
        canvas.drawLines(arrowPoints, paintArrow);
    }

    @Override
    public void applyTheme(Theme theme) {
        paintDate.setColor(theme.titleColor);
        paintName.setColor(theme.titleColor);
        paintPercent.setColor(theme.titleColor);
        sumColor = theme.titleColor;
        isDay = theme.getId() == Theme.DAY;
        paintArrow.setColor(theme.tooltipArrow);
    }
}