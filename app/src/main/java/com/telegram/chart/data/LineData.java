package com.telegram.chart.data;

public class LineData {
    private final String name;
    private final int color;
    private final long[] y;
    private final long maxY;
    private final long minY;

    public LineData(String name, int color, long[] points, long maxY, long minY) {
        this.name = name;
        this.color = color;
        this.y = points;
        this.maxY = maxY;
        this.minY = minY;
    }

    public String getName() {
        return name;
    }

    public int getColor() {
        return color;
    }

    public long[] getY() {
        return y;
    }

    public long getY(int num) {
        return y[num];
    }

    public long getMaxY() {
        return maxY;
    }

    public long getMinY() {
        return minY;
    }

//    public long getMaxY(float lower, float upper) {
//
//    }

    public int size() {
        return y.length;
    }
}