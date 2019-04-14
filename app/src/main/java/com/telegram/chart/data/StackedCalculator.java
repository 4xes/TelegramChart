package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;

public class StackedCalculator implements Calculator {

    @Override
    public int max(Chart chart) {
        int max = 0;
        for (int i = 0; i < chart.x.length; i++) {
            int iMax = 0;
            for (int id = 0; id < chart.data.length; id++) {
                if (chart.visible[id]) {
                    iMax += chart.data[id].y[i];
                }
            }
            if (max < iMax) {
                max = iMax;
            }
        }
        return max;
    }

    @Override
    public int max(Chart chart, int id) {
        int max = 0;
        for (int i = 0; i < chart.x.length; i++) {
            if (max < chart.data[id].y[i]) {
                max = chart.data[id].y[i];
            }
        }
        return max;
    }

    @Override
    public int max(Chart chart, Range range) {
        int max = 0;

        final int lower = chart.getLower(range.start);
        final int upper = chart.getUpper(range.end);
        for (int i = lower; i < upper; i++) {
            int iMax = 0;
            for (int id = 0; id < chart.data.length; id++) {
                if (chart.visible[id]) {
                    iMax += chart.data[id].y[i];
                }
            }
            if (max < iMax) {
                max = iMax;
            }
        }
        return max;
    }

    @Override
    public int max(Chart chart, int id, Range range) {
        int max = 0;
        final int lower = chart.getLower(range.start);
        final int upper = chart.getUpper(range.end);
        for (int i = lower; i < upper; i++) {
            if (max < chart.data[id].y[i]) {
                max = chart.data[id].y[i];
            }
        }
        return max;
    }

    @Override
    public int min(Chart chart) {
        return 0;
    }

    @Override
    public int min(Chart chart, Range range) {
        return 0;
    }

    @Override
    public int min(Chart chart, int id, Range range) {
        return 0;
    }

    @Override
    public int min(Chart chart, int id) {
        return 0;
    }
}
