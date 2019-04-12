package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
class PreviewStackedBarRender extends BaseStackedBarRender {

    public PreviewStackedBarRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPreviewStackedBars(chart, matrix);
    }
}