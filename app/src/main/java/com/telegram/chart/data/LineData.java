package com.telegram.chart.data;

public class LineData {
    private final String name;
    private final int color;
    public final int[] y;
    private final int maxY;
    private final int minY;

    int tempLowerId = -1;
    int tempUpperId = -1;
    int tempRangeMaxY = Integer.MIN_VALUE;

    public LineData(String name, int color, int[] points, int maxY, int minY) {
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

    public int[] getY() {
        return y;
    }

    public int getY(int num) {
        return y[num];
    }

    public int getMaxY() {
        return maxY;
    }

    public static int getLowerIndex(float lower, int maxIndex) {
        return (int) Math.ceil(lower * (float)(maxIndex));
    }

    public static int getUpperIndex(float upper, int maxIndex) {
        return (int) Math.floor(upper * (float)(maxIndex));
    }

    public int getMaxY(float lower, float upper) {
        int loverId = LineData.getLowerIndex(lower, y.length - 1);
        int upperId = LineData.getUpperIndex(upper, y.length - 1);

        if (tempRangeMaxY != Integer.MIN_VALUE) {
            if (tempLowerId == loverId && tempUpperId == upperId) {
                return tempRangeMaxY;
            }
        }
        if (loverId == upperId) {
            return y[loverId];
        }
        int max = y[loverId];
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