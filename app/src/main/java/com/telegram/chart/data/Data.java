package com.telegram.chart.data;

public class Data {
    public final String name;
    public final int color;
    public final int[] data;
    public final int max;
    public final int min;

    private int tempLowerId = -1;
    private int tempUpperId = -1;
    private int tempRangeMaxY = Integer.MIN_VALUE;

    public Data(String name, int color, int[] data, int max, int min) {
        this.name = name;
        this.color = color;
        this.data = data;
        this.max = max;
        this.min = min;
    }

    public int getLower(float lower) {
        return (int) Math.ceil(lower * (float)(data.length - 1));
    }

    public int getUpper(float upper) {
        return (int) Math.floor(upper * (float)(data.length - 1));
    }

    public int getMax(float lower, float upper) {
        int loverId = getLower(lower);
        int upperId = getUpper(upper);

        if (tempRangeMaxY != Integer.MIN_VALUE) {
            if (tempLowerId == loverId && tempUpperId == upperId) {
                return tempRangeMaxY;
            }
        }
        if (loverId == upperId) {
            return data[loverId];
        }
        int max = data[loverId];
        for (int i = loverId + 1; i <= upperId; i++) {
            if (max < data[i]) {
                max = data[i];
            }
        }
        tempLowerId = loverId;
        tempUpperId = upperId;
        tempRangeMaxY = max;
        return max;
    }

}