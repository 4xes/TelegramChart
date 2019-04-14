package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_LONG;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_SHORT;

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
            preview.percentStart[id] = 1f;
            preview.percentCurrent[id] = preview.percentStart[id];
            preview.percentEnd[id] = 1f;

            chart.percentStart[id] = 1f;
            chart.percentCurrent[id] = chart.percentStart[id];
            chart.percentEnd[id] = 1f;
        }
        setAnimationStart();
    }

    @Override
    public void setAnimationStart() {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = 0f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = 1f;

            chart.alphaStart[id] = 0f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = 1f;
        }
    }

    @Override
    public void updateRange() {
        chart.resetScaleAnimation(ANIMATION_DURATION_SHORT);
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
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        int max = manager.chart.max();
        int maxRange = manager.chart.max(manager.range);
        maxCurrent = maxRange;
        chart.maxStart = chart.maxCurrent;
        chart.maxEnd = maxRange;
        preview.maxStart = preview.maxCurrent;
        preview.maxEnd = max;

        for (int id = 0; id < manager.countLines(); id++) {
            preview.percentStart[id] = chart.percentCurrent[id];
            preview.percentEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.percentStart[id] = chart.percentCurrent[id];
            chart.percentEnd[id] = manager.chart.visible[id] ? 1f : 0f;
        }

        updateAxisAnimation(maxRange);
    }
}