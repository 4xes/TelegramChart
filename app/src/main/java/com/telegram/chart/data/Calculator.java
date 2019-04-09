package com.telegram.chart.data;

import com.telegram.chart.view.chart.Range;

interface Calculator {
    int max(Chart chart);
    int max(Chart chart, Range range);
    int max(Chart chart, int id, Range range);
}