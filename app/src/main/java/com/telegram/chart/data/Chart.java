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

    public Chart(int[] x, Data[] data, String type) {
        this.x = x;
        this.data = data;
        this.type = type;
        this.visible = new boolean[data.length];
        Arrays.fill(visible, true);
    }

    public int getLower(float lower) {
        return (int) Math.ceil(lower * (float)(x.length - 1));
    }

    public int getUpper(float upper) {
        return (int) Math.floor(upper * (float)(x.length - 1));
    }

    public int max() {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < data.length; id++) {
            if (visible[id]) {
                final Data line = data[id];
                if (max < line.max) {
                    max = line.max;
                }
            }
        }
        return max;
    }

    public int count() {
        return data.length;
    }

    public int max(Range range) {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < count(); id++) {
            if (visible[id]) {
                final Data line = data[id];
                final int maxLine = line.getMax(range.start, range.end);
                if (max < maxLine) {
                    max = maxLine;
                }
            }
        }
        return max;
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
        int max = data[id].getMax(range.start, range.end);
        return toStepped(max);
    }
}
