package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class StackedBarRender extends BaseStackedBarRender {

    public StackedBarRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixStackedBars(chart, matrix);
    }
}