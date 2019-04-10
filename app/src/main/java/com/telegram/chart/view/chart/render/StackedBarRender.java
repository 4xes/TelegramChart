package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class StackedBarRender extends BaseStackedBarRender {

    public StackedBarRender(GraphManager manager) {
        super(manager);
    }

    @Override
    protected int getLower(RectF chart, RectF visible) {
        return 0;
    }

    @Override
    protected int getUpper(RectF chart, RectF visible) {
        return manager.chart.x.length - 1;
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixStackedBars(chart, matrix);
    }
}