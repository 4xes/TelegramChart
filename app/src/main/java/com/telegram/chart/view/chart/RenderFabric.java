package com.telegram.chart.view.chart;

import com.telegram.chart.data.Chart;

public class RenderFabric {

    public static BaseRender[] getCharts(GraphManager graphManager) {
        BaseRender[] renders = new BaseRender[graphManager.countLines()];
        for (int id = 0; id < graphManager.countLines(); id++) {
            switch (graphManager.chart.type) {
                case Chart.TYPE_LINE:
                    renders[id] = new LineRender(id, graphManager);
                    break;
                case Chart.TYPE_LINE_SCALED:
                    renders[id] = new LineRender(id, graphManager);
                    break;
                case Chart.TYPE_BAR_STACKED:
                    renders[id] = new StackedBarRender(id, graphManager);
                    break;
            }
        }
        return renders;
    }


    public static BaseRender[] getPreviews(GraphManager graphManager) {
        BaseRender[] renders = new BaseRender[graphManager.countLines()];
        for (int id = 0; id < graphManager.countLines(); id++) {
            switch (graphManager.chart.type) {
                case Chart.TYPE_LINE:
                    renders[id] = new PreviewLineRender(id, graphManager);
                    break;
                case Chart.TYPE_LINE_SCALED:
                    renders[id] = new PreviewLineRender(id, graphManager);
                    break;
                case Chart.TYPE_BAR_STACKED:
                    renders[id] = new PreviewLineRender(id, graphManager);
                    break;
            }
        }
        return renders;
    }
}
