package com.telegram.chart.data;

public class CalculatorFabric {

    public static Calculator getCalculator(Chart chart) {
        switch (chart.type) {
            case Chart.TYPE_LINE:
                return new LineCalculator();
            case Chart.TYPE_LINE_SCALED:
                return new LineCalculator();
            case Chart.TYPE_BAR:
                return new LineCalculator();
            case Chart.TYPE_BAR_STACKED:
                return new StackedCalculator();
            case Chart.TYPE_PERCENTAGE:
                return new PercentageCalculator();
            case Chart.TYPE_PIE:
                return new PercentageCalculator();
            default:
                throw new IllegalArgumentException();
        }
    }

}
