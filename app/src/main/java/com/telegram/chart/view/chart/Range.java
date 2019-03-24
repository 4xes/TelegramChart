package com.telegram.chart.view.chart;

public class Range {
    public float start = RANGE_START_INIT;
    public float end = RANGE_END_INIT;
    public float min = RANGE_MIN_INIT;

    public float range() {
        return end - start;
    }
    public static final int RANGE_MULTI = 8;
    public static final float RANGE_MIN_INIT = 1f / (float) RANGE_MULTI;
    public static final float RANGE_END_INIT = 1f;
    public static final float RANGE_START_INIT = 0f;
}