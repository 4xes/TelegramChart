package com.telegram.chart.data;

public class CalculatorFabric {

    public static Calculator getCalculator(Chart chart) {
        switch (chart.type) {
            case Chart.TYPE_LINE:
                return new LineCalculator();
            case Chart.TYPE_LINE_SCALED:
                return new LineCalculator();
            case Chart.TYPE_BAR_STACKED:
                return new StackedCalculator();
            default:
                throw new IllegalArgumentException();
        }
    }

}
