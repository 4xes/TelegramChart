package com.telegram.chart.view.chart;

public class Range {
    public float start = RANGE_START_INIT;
    public float end = RANGE_END_INIT;
    public float min = RANGE_MIN_INIT;
    public float length() {
        return end - start;
    }
    public static final float RANGE_MIN_INIT = 0.1f;
    public static final float RANGE_END_INIT = 1f;
    public static final float RANGE_START_INIT = RANGE_END_INIT - RANGE_MIN_INIT;
}