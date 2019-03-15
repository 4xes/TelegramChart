package com.telegram.chart.data;

import java.util.List;

public interface ChartsInteractor {
    List<ChartData> getCharts() throws Throwable;
}