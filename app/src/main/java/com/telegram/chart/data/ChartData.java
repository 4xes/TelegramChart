package com.telegram.chart.data;

public class ChartData {
    private final LineData[] lines;
    private final int[] x;

    public ChartData(LineData[] lines, int[] x) {
        this.lines = lines;
        this.x = x;
    }

    public LineData[] getLines() {
        return lines;
    }

    public int[] getX() {
        return x;
    }

    public int getY(int num) {
        return x[num];
    }
}
