package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PreviewPercentageRender extends BasePercentageRender {

    public PreviewPercentageRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPercentagePreview(chart, matrix);
    }
}