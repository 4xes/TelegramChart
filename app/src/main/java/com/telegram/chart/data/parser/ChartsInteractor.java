package com.telegram.chart.data.parser;

import com.telegram.chart.data.Chart;

public interface ChartsInteractor {
    Chart getChart(int number) throws Throwable;
}