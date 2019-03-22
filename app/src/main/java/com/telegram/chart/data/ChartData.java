package com.telegram.chart.data;

public class ChartData {
    private final LineData[] lines;
    private final long[] x;

    public ChartData(LineData[] lines, long[] x) {
        this.lines = lines;
        this.x = x;
    }

    public LineData[] getLines() {
        return lines;
    }

    public long[] getX() {
        return x;
    }

    public long getY(int num) {
        return x[num];
    }
}
