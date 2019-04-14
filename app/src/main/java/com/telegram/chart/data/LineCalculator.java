package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;

public class LineCalculator implements Calculator {

    @Override
    public int max(Chart chart) {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < chart.data.length; id++) {
            if (chart.visible[id]) {
                final Data data = chart.data[id];
                if (max < data.max) {
                    max = data.max;
                }
            }
        }
        return max;
    }

    @Override
    public int max(Chart chart, Range range) {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < chart.data.length; id++) {
            if (chart.visible[id]) {
                final int lower = chart.getLower(range.start);
                final int upper = chart.getUpper(range.end);
                final int maxLine = chart.data[id].max(lower, upper);
                if (max < maxLine) {
                    max = maxLine;
                }
            }
        }
        return max;
    }

    @Override
    public int max(Chart chart, int id) {
        return chart.data[id].max;
    }

    @Override
    public int max(Chart chart, int id, Range range) {
        final int lower = chart.getLower(range.start);
        final int upper = chart.getUpper(range.end);
        return chart.data[id].max(lower, upper);
    }

    @Override
    public int min(Chart chart) {
        int min = Integer.MAX_VALUE;

        for (int id = 0; id < chart.data.length; id++) {
            if (chart.visible[id]) {
                final Data data = chart.data[id];
                if (min > data.min) {
                    min = data.min;
                }
            }
        }
        return min;
    }

    @Override
    public int min(Chart chart, Range range) {
        int min = Integer.MAX_VALUE;

        for (int id = 0; id < chart.data.length; id++) {
            if (chart.visible[id]) {
                final int lower = chart.getLower(range.start);
                final int upper = chart.getUpper(range.end);
                final int minLine = chart.data[id].min(lower, upper);
                if (min > minLine) {
                    min = minLine;
                }
            }
        }
        return min;
    }

    @Override
    public int min(Chart chart, int id, Range range) {
        final int lower = chart.getLower(range.start);
        final int upper = chart.getUpper(range.end);
        return chart.data[id].min(lower, upper);
    }

    @Override
    public int min(Chart chart, int id) {
        return chart.data[id].min;
    }
}
