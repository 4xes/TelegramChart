package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_LONG;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_SHORT;

public class BarStateManager extends StateManager {
    public BarStateManager(GraphManager manager) {
        super(manager);
        this.chart = new LineState(manager.countLines());
        this.preview = new LineState(manager.countLines());

        int maxPreview = manager.chart.max();
        int maxRange = manager.chart.max(manager.range);
        int maxChart = maxStepped(maxRange);
        maxCurrent = maxRange;
        previousStep = step(maxRange);
        currentStep = previousStep;

        for (int id = 0; id < manager.countLines(); id++) {
            preview.yMaxStart[id] = maxPreview;
            preview.yMaxCurrent[id] = preview.yMaxStart[id];
            preview.yMaxEnd[id] = maxPreview;

            preview.alphaStart[id] = 1f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = 1f;

            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.alphaStart[id] = 1f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = 1f;

            chart.yMaxStart[id] = maxChart;
            chart.yMaxCurrent[id] = chart.yMaxStart[id];
            chart.yMaxEnd[id] = maxChart;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }
        setAnimationStart();
    }

    @Override
    public void setAnimationStart() {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);

        for (int id = 0; id < manager.countLines(); id++) {
            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }
    }

    @Override
    public void updateRange() {
        chart.resetScaleAnimation(ANIMATION_DURATION_SHORT);

        int maxRange = manager.chart.max(manager.range);
        updateAxisAnimation(maxRange);
        maxCurrent = maxRange;

        int maxStepped = maxStepped(maxRange);
        for (int id = 0; id < manager.countLines(); id++) {
            if (manager.chart.visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxStepped;
            }
        }

    }

    @Override
    public void updateAxisAnimation(int maxChart) {
        float step = step(maxChart);
        if (currentStep != step) {
            previousStep = currentStep;
            currentStep = step;
            resetAxisAnimation();
        }
    }

    @Override
    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        int max = manager.chart.max();
        int maxRange = manager.chart.max(manager.range);

        if (maxRange == Integer.MIN_VALUE) {
            maxRange = manager.chart.max(targetId, manager.range);
        }
        maxCurrent = maxRange;
        int maxStepped = maxStepped(maxRange);
        if (max == Integer.MIN_VALUE) {
            max = manager.chart.data[targetId].max;
        }

        int targetMaxStepped = manager.chart.stepMax(targetId, manager.range);
        if (maxStepped == Integer.MIN_VALUE) {
            maxStepped = targetMaxStepped;
        }

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            if ((targetId != id || manager.chart.data[targetId].max == max)) {
                preview.yMaxStart[id] = preview.yMaxCurrent[id];
                preview.yMaxEnd[id] = max;

                if (manager.chart.data[targetId].max == max && targetId == id) {
                    preview.yMaxStart[id] = max;
                    preview.yMaxCurrent[id] = max;
                    preview.yMaxEnd[id] = max;
                }
            }

            chart.yMaxStart[id] = chart.yMaxCurrent[id];
            chart.yMaxEnd[id] = maxStepped;
        }

        if (targetMaxStepped == maxStepped) {
            chart.yMaxStart[targetId] = chart.yMaxCurrent[targetId];
            chart.yMaxEnd[targetId] = manager.chart.visible[targetId] ? maxStepped : maxStepped / 4;
        }

        updateAxisAnimation(maxRange);
    }
}