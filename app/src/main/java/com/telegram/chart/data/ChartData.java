package com.telegram.chart.data;

public class ChartData {
    private final LineData[] lines;
    private final long[] x;
    private final long maxY;
    private final long minY;

    public ChartData(LineData[] lines, long[] x) {
        this.lines = lines;
        this.x = x;
        long max = Long.MIN_VALUE;
        long min = Long.MAX_VALUE;

        for (int li = 0; li < lines.length; li++) {
            final LineData line = lines[li];
            if (max < line.getMaxY()) {
                max = line.getMaxY();
            }
            if (min > line.getMinY()) {
                min = line.getMinY();
            }
        }
        this.maxY = max;
        this.minY = min;
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

    public long getMaxY() {
        return maxY;
    }

    public long getMinY() {
        return minY;
    }
}
