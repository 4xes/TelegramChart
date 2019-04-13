package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PercentageRender extends BasePercentageRender {

    public PercentageRender(GraphManager manager) {
        super(manager, false);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPercentage(chart, matrix);
    }
}