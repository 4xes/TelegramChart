package com.telegram.chart.view.chart.state;

import com.telegram.chart.data.Chart;
import com.telegram.chart.view.chart.GraphManager;

public class StateFabric {
    public static StateManager getStateManager(GraphManager manager) {
        switch (manager.chart.type) {
            case Chart.TYPE_LINE:
                return new LineStateManager(manager);
            case Chart.TYPE_LINE_SCALED:
                return new LineStateManager(manager);
            case Chart.TYPE_BAR:
                return new LineStateManager(manager);
            case Chart.TYPE_BAR_STACKED:
                return new PercentageStateManager(manager);
            case Chart.TYPE_PERCENTAGE:
                return new PercentageStateManager(manager);
            default:
                throw new IllegalArgumentException();
        }
    }
}
