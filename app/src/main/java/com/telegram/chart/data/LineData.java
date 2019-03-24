package com.telegram.chart.data;

public class LineData {
    private final String name;
    private final int color;
    private final long[] y;
    private final long maxY;
    private final long minY;

    int tempLowerId = -1;
    int tempUpperId = -1;
    long tempRangeMaxY = Long.MIN_VALUE;

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

    public static int getLowerIndex(float lower, int maxIndex) {
        return (int) Math.ceil(lower * (float)(maxIndex));
    }

    public static int getUpperIndex(float upper, int maxIndex) {
        return (int) Math.floor(upper * (float)(maxIndex));
    }

    public long getMaxY(float lower, float upper) {
        int loverId = LineData.getLowerIndex(lower, y.length - 1);
        int upperId = LineData.getUpperIndex(upper, y.length - 1);

        if (tempRangeMaxY != Long.MIN_VALUE) {
            if (tempLowerId == loverId && tempUpperId == upperId) {
                return tempRangeMaxY;
            }
        }
        if (loverId == upperId) {
            return y[loverId];
        }
        long max = y[loverId];
        for (int i = loverId + 1; i <= upperId; i++) {
            if (max < y[i]) {
                max = y[i];
            }
        }
        tempLowerId = loverId;
        tempUpperId = upperId;
        tempRangeMaxY = max;
        return max;
    }
}