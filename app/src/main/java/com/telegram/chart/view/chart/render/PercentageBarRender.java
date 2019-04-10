package com.telegram.chart.view.chart.render;

import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;

class PercentageBarRender extends BasePercentageBarRender {

    public PercentageBarRender(GraphManager manager) {
        super(manager);
    }

    @Override
    protected void initPaints() {
        super.initPaints();
        paintBars.setAntiAlias(true);
    }

    @Override
    protected int getLower(RectF chart, RectF visible) {
        final float sectionWidth = manager.sectionWidth(chart.width());
        final int addIndexLower= (int) Math.rint((chart.left - visible.left) / sectionWidth);
        final int lower = manager.chart.getLower(manager.range.start) - 1 - addIndexLower;
        if (lower < 0) {
            return 0;
        }
        return lower;
    }

    @Override
    protected int getUpper(RectF chart, RectF visible) {
        final float sectionWidth = manager.sectionWidth(chart.width());
        final int addIndexUpper = (int) Math.rint((visible.right - chart.right) / sectionWidth);
        final int upper = manager.chart.getUpper(manager.range.end) + 1 + addIndexUpper;
        if (upper > manager.chart.x.length - 1) {
            return manager.chart.x.length - 1;
        }
        return upper;
    }

    @Override
    protected void updateMatrix(RectF chart) {
        manager.matrixPercentageBars(chart, matrix);
    }
}