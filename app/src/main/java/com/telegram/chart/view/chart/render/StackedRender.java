package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class StackedRender extends BaseStackedRender {

    public StackedRender(GraphManager manager) {
        super(manager, false);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixStacked(chart, matrix);
    }
}