package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class BarRender extends BaseBarRender {
    public BarRender(GraphManager manager) {
        super(manager, false);
        calculateBars();
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixBar(chart, matrix);
    }
}