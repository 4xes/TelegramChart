package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;
import com.telegram.chart.view.chart.XYRender;

import java.util.Arrays;

public class Chart {
    public static final String TYPE_LINE = "line";
    public static final String TYPE_BAR_STACKED = "bar_stacked";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_LINE_SCALED = "line_scaled";
    public final Data[] data;
    public final int[] x;
    public final String type;
    public final boolean[] visible;
    public final Calculator calculator;

    public Chart(int[] x, Data[] data, String type) {
        this.x = x;
        this.data = data;
        this.type = type;
        this.visible = new boolean[data.length];
        Arrays.fill(visible, true);
        calculator = CalculatorFabric.getCalculator(this);
    }

    public int getLower(float lower) {
        return (int) Math.ceil(lower * (float)(x.length - 1));
    }

    public int getUpper(float upper) {
        return (int) Math.floor(upper * (float)(x.length - 1));
    }

    public int max() {
        return calculator.max(this);
    }

    public int max(Range range) {
        return calculator.max(this, range);
    }

    public static int toStepped(int max) {
        if (max != Integer.MIN_VALUE) {
            float step = step(max);
            return (int) Math.floor(step * (XYRender.GRID));
        }
        return max;
    }

    public static float step(int max) {
        return XYRender.calculateStep(0, max, XYRender.GRID);
    }

    public int stepMax(int id, Range range) {
        return toStepped(calculator.max(this, id, range));
    }
}
