package com.telegram.chart.view.chart.render;

import com.telegram.chart.data.Chart;
import com.telegram.chart.view.chart.GraphManager;

public class RenderFabric {

    public static Render getChart(GraphManager graphManager) {
        Render render = null;
        switch (graphManager.chart.type) {
            case Chart.TYPE_LINE:
                render = new LineRender(graphManager);
                break;
            case Chart.TYPE_LINE_SCALED:
                render = new LineRender(graphManager);
                break;
            case Chart.TYPE_BAR:
                render = new BarRender(graphManager);
                break;
            case Chart.TYPE_BAR_STACKED:
                render = new StackedRender(graphManager);
                break;
            case Chart.TYPE_PERCENTAGE:
                render = new PieRender(graphManager);
                break;
        }
        if (render == null) {
            throw new IllegalArgumentException();
        }
        return render;
    }

    public static Render getPreview(GraphManager graphManager) {
        Render render = null;
        switch (graphManager.chart.type) {
            case Chart.TYPE_LINE:
                render = new PreviewLineRender(graphManager);
                break;
            case Chart.TYPE_LINE_SCALED:
                render = new PreviewLineRender(graphManager);
                break;
            case Chart.TYPE_BAR:
                render = new PreviewBarRender(graphManager);
                break;
            case Chart.TYPE_BAR_STACKED:
                render = new PreviewStackedRender(graphManager);
                break;
            case Chart.TYPE_PERCENTAGE:
                render = new PreviewPercentageRender(graphManager);
                break;
        }
        if (render == null) {
            throw new IllegalArgumentException();
        }
        return render;
    }
}
