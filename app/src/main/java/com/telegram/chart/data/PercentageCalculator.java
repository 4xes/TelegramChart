package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;

public class PercentageCalculator implements Calculator {
    @Override
    public int max(Chart chart) {
        int max = Integer.MIN_VALUE;
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
    public int max(Chart chart, Range range) {
        int max = Integer.MIN_VALUE;

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
        return max(chart, range);
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
}
