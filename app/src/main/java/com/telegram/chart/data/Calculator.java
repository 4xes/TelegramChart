package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;

interface Calculator {
    int max(Chart chart);
    int max(Chart chart, int id);
    int max(Chart chart, Range range);
    int max(Chart chart, int id, Range range);

    int min(Chart chart);
    int min(Chart chart, Range range);
    int min(Chart chart, int id, Range range);
}