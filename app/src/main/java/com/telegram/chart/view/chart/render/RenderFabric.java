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
            case Chart.TYPE_BAR_STACKED:
                render = new StackedBarRender(graphManager);
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
            case Chart.TYPE_BAR_STACKED:
                render = new PreviewLineRender(graphManager);
                break;
        }
        if (render == null) {
            throw new IllegalArgumentException();
        }
        return render;
    }
}
