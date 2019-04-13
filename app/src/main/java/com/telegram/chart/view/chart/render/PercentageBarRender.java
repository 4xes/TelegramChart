package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PercentageBarRender extends BasePercentageBarRender {

    public PercentageBarRender(GraphManager manager) {
        super(manager, false);
    }

    @Override
    protected void initPaints() {
        super.initPaints();
        for (int id = 0; id < manager.countLines(); id++) {
            paint[id].setAntiAlias(true);
        }
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPercentageBars(chart, matrix);
    }
}