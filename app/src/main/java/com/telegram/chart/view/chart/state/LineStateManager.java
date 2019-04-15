package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.data.Chart.minStepped;
import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.view.chart.XYRender.GRID;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_LONG;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_SHORT;

public class LineStateManager extends StateManager {
    public LineStateManager(GraphManager manager) {
        super(manager);
        this.chart = new LineState(manager.countLines());
        this.preview = new LineState(manager.countLines());

        int maxPreview = manager.chart.max();
        int minPreview = manager.chart.min();
        int maxRange = manager.chart.max(manager.range);
        int minRange = manager.chart.min(manager.range);
        int maxChart = maxStepped(maxRange);
        float step = step(maxRange);
        int minChart = minStepped(minRange, step);

        maxCurrent = maxRange;
        minCurrent = minChart;
        previousMinChart = minChart;
        currentMinChart = previousMinChart;
        previousMaxChart = maxChart;
        currentMaxChart = previousMaxChart;

        previousStep = (maxChart - minChart) / (float) GRID;
        currentStep = previousStep;

        for (int id = 0; id < manager.countLines(); id++) {
            preview.yMaxStart[id] = maxPreview;
            preview.yMaxCurrent[id] = preview.yMaxStart[id];
            preview.yMaxEnd[id] = maxPreview;

            preview.yMinStart[id] = minPreview;
            preview.yMinCurrent[id] = preview.yMinStart[id];
            preview.yMinEnd[id] = minPreview;

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

            chart.yMinStart[id] = minChart;
            chart.yMinCurrent[id] = chart.yMinStart[id];
            chart.yMinEnd[id] = minChart;

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
        super.updateRange();
        chart.resetScaleAnimation(ANIMATION_DURATION_SHORT);

        int maxRange = manager.chart.max(manager.range);
        int minRange = manager.chart.min(manager.range);


        int maxChart = maxStepped(maxRange);
        float step = step(maxRange);
        int minChart = minStepped(minRange, step);
        maxCurrent = maxRange;
        minCurrent = minChart;
        updateAxisAnimation(minChart, maxChart);

        for (int id = 0; id < manager.countLines(); id++) {
            if (manager.chart.visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxChart;

                chart.yMinStart[id] = chart.yMinCurrent[id];
                chart.yMinEnd[id] = minChart;
            }
        }

    }

    public void updateAxisAnimation(int minChart, int maxChart) {
        float step = (maxChart - minChart) / (float) GRID;
        if (currentMaxChart != maxChart) {
            previousStep = currentStep;
            currentStep = step;
            resetAxisAnimation();
        }
        if (currentMaxChart != maxChart) {
            previousMaxChart = currentMaxChart;
            currentMaxChart = maxChart;
            resetAxisAnimation();
        }
        if (currentMinChart != minChart) {
            previousMinChart = currentMinChart;
            currentMinChart = minChart;
            resetAxisAnimation();
        }
    }

    @Override
    public void updateAxisAnimation(int maxChart) {

    }

    @Override
    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        int max = manager.chart.max();
        int min = manager.chart.min();
        int maxRange = manager.chart.max(manager.range);
        int minRange = manager.chart.min(manager.range);

        if (maxRange == Integer.MIN_VALUE) {
            maxRange = manager.chart.max(targetId, manager.range);
        }

        if (minRange == Integer.MAX_VALUE) {
            minRange = manager.chart.min(targetId, manager.range);
        }

        maxCurrent = maxRange;
        float step = step(maxRange);
        int minChart = minStepped(minRange, step);
        minCurrent = minChart;

        int maxChart = maxStepped(maxRange);
        if (max == Integer.MIN_VALUE) {
            max = manager.chart.data[targetId].max;
        }

        int targetMaxStepped = manager.chart.stepMax(targetId, manager.range);
        if (maxChart == Integer.MIN_VALUE) {
            maxChart = targetMaxStepped;
        }

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.yMinStart[id] = chart.yMinCurrent[id];
            chart.yMinEnd[id] = minChart;

            if ((targetId != id || manager.chart.data[targetId].max == max)) {
                preview.yMaxStart[id] = preview.yMaxCurrent[id];
                preview.yMaxEnd[id] = max;

                if (manager.chart.data[targetId].max == max && targetId == id) {
                    preview.yMaxStart[id] = max;
                    preview.yMaxCurrent[id] = max;
                    preview.yMaxEnd[id] = max;
                }

                if (manager.chart.data[targetId].min == min && targetId == id) {
                    preview.yMinStart[id] = min;
                    preview.yMinCurrent[id] = min;
                    preview.yMinEnd[id] = min;
                }
            }

            chart.yMaxStart[id] = chart.yMaxCurrent[id];
            chart.yMaxEnd[id] = maxChart;
        }

        updateAxisAnimation(minChart, maxChart);
    }
}