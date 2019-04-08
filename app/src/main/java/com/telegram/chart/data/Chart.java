package com.telegram.chart.data;

public class Chart {
    public static final String TYPE_LINE = "line";
    public static final String TYPE_BAR_STACKED = "bar_stacked";
    public static final String TYPE_BAR = "bar";
    public static final String TYPE_LINE_SCALED = "line_scaled";
    public final Data[] data;
    public final int[] x;
    public final String type;
    public Chart(int[] x, Data[] data, String type) {
        this.x = x;
        this.data = data;
        this.type = type;
    }

    public int getLower(float lower) {
        return (int) Math.ceil(lower * (float)(x.length - 1));
    }

    public int getUpper(float upper) {
        return (int) Math.floor(upper * (float)(x.length - 1));
    }
}
