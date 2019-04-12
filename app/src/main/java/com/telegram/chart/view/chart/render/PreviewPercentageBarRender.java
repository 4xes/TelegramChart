package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PreviewPercentageBarRender extends BasePercentageBarRender {

    public PreviewPercentageBarRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPercentagePreviewBars(chart, matrix);
    }
}