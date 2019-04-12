package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PreviewBarRender extends BaseBarRender {

    public PreviewBarRender(GraphManager manager) {
        super(manager, true);
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixBarPreview(chart, matrix);
    }
}