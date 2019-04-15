package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;
import com.telegram.chart.view.chart.XYRender;

import java.util.Arrays;

public class Chart {
    public static final String TYPE_LINE = "line";
    public static final String TYPE_BAR_STACKED = "bar_stacked";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_LINE_SCALED = "line_scaled";
    public static final String TYPE_PERCENTAGE = "percentage";
    public final Data[] data;
    public final int[] x;
    public final String type;
    public final boolean[] visible;
    public final Calculator calculator;
    public final boolean isPercentage;
    public final boolean isLine;
    public final boolean isScaled;
    public final boolean isStacked;
    public final int max;
    public final String maxLengthName;

    public Chart(int[] x, Data[] data, String type) {
        this.x = x;
        this.data = data;
        this.type = type;
        this.visible = new boolean[data.length];
        Arrays.fill(visible, true);
        calculator = CalculatorFabric.getCalculator(this);
        isPercentage = Chart.TYPE_PERCENTAGE.equals(type);
        isLine = Chart.TYPE_LINE.equals(type) || Chart.TYPE_LINE_SCALED.equals(type);
        isScaled = Chart.TYPE_LINE_SCALED.equals(type);
        isStacked = Chart.TYPE_BAR_STACKED.equals(type);

        int max = Integer.MIN_VALUE;
        String maxLengthName = "";
        for (int id = 0; id < data.length; id++) {
            if (data[id].name.length() > maxLengthName.length()) {
                maxLengthName = data[id].name;
            }
            if (data[id].max > max) {
                max = data[id].max;
            }
        }
        this.max = max;
        this.maxLengthName = maxLengthName;
    }

    public int getLower(float lower) {
        return (int) Math.ceil(lower * (float)(x.length - 1));
    }

    public int getUpper(float upper) {
        return (int) Math.floor(upper * (float)(x.length - 1));
    }

    public int max(int id) {
        return calculator.max(this, id);
    }

    public int max() {
        return calculator.max(this);
    }

    public int max(Range range) {
        return calculator.max(this, range);
    }

    public int min(Range range) {
        return calculator.min(this, range);
    }

    public int min() {
        return calculator.min(this);
    }

    public int min(int id) {
        return calculator.min(this, id);
    }

    public int max(int id, Range range) {
        return calculator.max(this, id, range);
    }

    public int min(int id,Range range) {
        return calculator.min(this, id, range);
    }


    public static int maxStepped(int max) {
        if (max != Integer.MIN_VALUE) {
            float step = step(max);
            return (int) Math.floor(step * (XYRender.GRID));
        }
        return max;
    }

    public static int minStepped(int min, float step) {
        return (int) ((int) (min / step) * step);
    }

    public static float step(int max) {
        return XYRender.calculateStep(0, max, XYRender.GRID);
    }

    public int stepMax(int id, Range range) {
        return maxStepped(calculator.max(this, id, range));
    }

    public int stepMin(int id, Range range, float step) {
        return minStepped(calculator.min(this, id, range), step);
    }

}
