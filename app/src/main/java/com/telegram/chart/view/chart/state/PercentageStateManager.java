package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.view.chart.state.State.DURATION_LONG;
import static com.telegram.chart.view.chart.state.State.DURATION_SHORT;

public class PercentageStateManager extends StateManager {

    public PercentageStateManager(GraphManager manager) {
        super(manager);
        this.chart = new StackedState(manager.countLines());
        this.preview = new StackedState(manager.countLines());

        int max = manager.chart.max();
        int maxRange = manager.chart.max(manager.range);
        maxCurrent = maxRange;
        float step = step(maxRange);
        int maxChart = maxStepped(maxRange);

        previousStep = step;
        currentStep = previousStep;

        preview.maxStart = max;
        preview.maxEnd = max;
        preview.maxCurrent = max;
        chart.maxStart = maxChart;
        chart.maxEnd = maxChart;
        chart.maxCurrent = maxChart;

        for (int id = 0; id < manager.countLines(); id++) {
            preview.percentStart[id] = manager.chart.visible[id] ? 1f: 0f;
            preview.percentCurrent[id] = preview.percentStart[id];
            preview.percentEnd[id] = manager.chart.visible[id] ? 1f: 0f;

            chart.percentStart[id] = manager.chart.visible[id] ? 1f: 0f;
            chart.percentCurrent[id] = chart.percentStart[id];
            chart.percentEnd[id] = manager.chart.visible[id] ? 1f: 0f;
        }
        setAnimationStart();
    }

    @Override
    public void setAnimationStart() {
        chart.resetScaleAnimation(DURATION_LONG);
        preview.resetScaleAnimation(DURATION_LONG);

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = manager.chart.visible[id] ? 0f: 1f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = manager.chart.visible[id] ? 1f: 0f;

            chart.alphaStart[id] = manager.chart.visible[id] ? 0f: 1f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = manager.chart.visible[id] ? 1f: 0f;

            if (manager.chart.isStacked) {
                preview.multiStart[id] = 0f;
                preview.multiCurrent[id] = preview.multiStart[id];
                preview.multiEnd[id] = 1f;

                chart.multiStart[id] = 0f;
                chart.multiCurrent[id] = chart.multiStart[id];
                chart.multiEnd[id] = 1f;
            }
        }
    }

    @Override
    public void updateRange() {
        super.updateRange();
        chart.resetScaleAnimation(DURATION_SHORT);
        int maxRange = manager.chart.max(manager.range);
        maxCurrent = maxRange;
        updateAxisAnimation(maxRange);
        int maxStepped = maxStepped(maxRange);
        chart.maxStart = chart.maxCurrent;
        chart.maxEnd = maxStepped;
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

    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(DURATION_LONG);
        chart.resetFadingAnimation(DURATION_LONG);
        preview.resetScaleAnimation(DURATION_LONG);
        preview.resetFadingAnimation(DURATION_LONG);

        int max = manager.chart.max();

        if (max == 0) {
            max = manager.chart.max(targetId, manager.range);
        }

        preview.maxStart = preview.maxCurrent;
        preview.maxEnd = max;

        updateRange();

        boolean hide = manager.countVisible() == 0;
        for (int id = 0; id < manager.countLines(); id++) {
            preview.percentStart[id] = chart.percentCurrent[id];
            preview.percentEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = hide ? 0f : 1f;

            chart.percentStart[id] = chart.percentCurrent[id];
            chart.percentEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = hide ? 0f : 1f;
        }
    }
}