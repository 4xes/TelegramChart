package com.telegram.chart.data;

public class Data {
    public final String name;
    public final int color;
    public final int colorNight;
    public final int buttonColor;
    public final int buttonColorNight;
    public final int tooltipColor;
    public final int tooltipColorNight;
    public final int[] y;
    public final int max;
    public final int min;

    private int tempLowerId = -1;
    private int tempUpperId = -1;
    private int tempRangeMaxY = Integer.MIN_VALUE;

    public Data(String name, int color, int colorNight, int buttonColor, int buttonColorNight, int tooltipColor, int tooltipColorNight, int[] y, int max, int min) {
        this.name = name;
        this.color = color;
        this.colorNight = colorNight;
        this.buttonColor = buttonColor;
        this.buttonColorNight = buttonColorNight;
        this.tooltipColor = tooltipColor;
        this.tooltipColorNight = tooltipColorNight;
        this.y = y;
        this.max = max;
        this.min = min;
    }

    public int max(int lower, int upper) {
        if (tempRangeMaxY != Integer.MIN_VALUE) {
            if (tempLowerId == lower && tempUpperId == upper) {
                return tempRangeMaxY;
            }
        }
        if (lower == upper) {
            return y[lower];
        }
        int max = y[lower];
        for (int i = lower + 1; i <= upper; i++) {
            if (max < y[i]) {
                max = y[i];
            }
        }
        tempLowerId = lower;
        tempUpperId = upper;
        tempRangeMaxY = max;
        return max;
    }

}