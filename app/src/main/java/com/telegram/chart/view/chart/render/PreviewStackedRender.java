package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
class PreviewStackedRender extends BaseStackedRender {

    public PreviewStackedRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPreviewStacked(chart, matrix);
    }
}