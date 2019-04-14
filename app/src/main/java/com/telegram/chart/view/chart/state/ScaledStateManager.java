package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.data.Chart.maxStepped;
import static com.telegram.chart.data.Chart.minStepped;
import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.view.chart.XYRender.GRID;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_LONG;
import static com.telegram.chart.view.chart.state.State.ANIMATION_DURATION_SHORT;
import static com.telegram.chart.view.chart.state.State.ANIMATION_TICK;

public class ScaledStateManager extends StateManager {
    public long executedAxisTime2 = ANIMATION_DURATION_LONG;
    public long durationAxis2 = ANIMATION_DURATION_LONG;
    public int maxCurrent2;
    public int previousMinChart2 = 0;
    public int previousMaxChart2 = 0;
    public int currentMinChart2 = 0;
    public int currentMaxChart2 = 0;
    public int minCurrent2 = 0;
    public float previousStep2;
    public float currentStep2;

    public ScaledStateManager(GraphManager manager) {
        super(manager);
        this.chart = new LineState(manager.countLines());
        this.preview = new LineState(manager.countLines());

        int maxPreview = manager.chart.max(0);
        int minPreview = manager.chart.min(0);
        int maxRange = manager.chart.max(0, manager.range);
        int minRange = manager.chart.min(0, manager.range);
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


        chart.yMaxStart[0] = maxChart;
        chart.yMaxCurrent[0] = chart.yMaxStart[0];
        chart.yMaxEnd[0] = maxChart;

        chart.yMinStart[0] = minChart;
        chart.yMinCurrent[0] = chart.yMinStart[0];
        chart.yMinEnd[0] = minChart;

        preview.yMaxStart[0] = maxPreview;
        preview.yMaxCurrent[0] = preview.yMaxStart[0];
        preview.yMaxEnd[0] = maxPreview;

        preview.yMinStart[0] = minPreview;
        preview.yMinCurrent[0] = preview.yMinStart[0];
        preview.yMinEnd[0] = minPreview;

        for (int id = 0; id < manager.countLines(); id++) {

            preview.alphaStart[id] = 1f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = 1f;

            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.alphaStart[id] = 1f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = 1f;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }

        //second


        int maxPreview2 = manager.chart.max(1);
        int minPreview2 = manager.chart.min(1);
        int maxRange2 = manager.chart.max(1, manager.range);
        int minRange2 = manager.chart.min(1, manager.range);
        int maxChart2 = maxStepped(maxRange2);
        float step2 = step(maxRange2);
        int minChart2 = minStepped(minRange2, step2);

        maxCurrent2 = maxRange2;
        minCurrent2 = minChart2;
        previousMinChart2 = minChart2;
        currentMinChart2 = previousMinChart2;
        previousMaxChart2 = maxChart2;
        currentMaxChart2 = previousMaxChart2;

        previousStep2 = (maxChart2 - minChart2) / (float) GRID;
        currentStep2 = previousStep2;


        chart.yMaxStart[1] = maxChart2;
        chart.yMaxCurrent[1] = chart.yMaxStart[1];
        chart.yMaxEnd[1] = maxChart2;

        chart.yMinStart[1] = minChart2;
        chart.yMinCurrent[1] = chart.yMinStart[1];
        chart.yMinEnd[1] = minChart2;

        preview.yMaxStart[1] = maxPreview2;
        preview.yMaxCurrent[1] = preview.yMaxStart[1];
        preview.yMaxEnd[1] = maxPreview2;

        preview.yMinStart[1] = minPreview2;
        preview.yMinCurrent[1] = preview.yMinStart[1];
        preview.yMinEnd[1] = minPreview2;

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

        int maxRange = manager.chart.max(0, manager.range);
        int minRange = manager.chart.min(0, manager.range);


        int maxChart = maxStepped(maxRange);
        float step = step(maxRange);
        int minChart = minStepped(minRange, step);
        maxCurrent = maxRange;
        minCurrent = minChart;
        updateAxisAnimation(minChart, maxChart);

        if (manager.chart.visible[0]) {
            chart.yMaxStart[0] = chart.yMaxCurrent[0];
            chart.yMaxEnd[0] = maxChart;

            chart.yMinStart[0] = chart.yMinCurrent[0];
            chart.yMinEnd[0] = minChart;
        }

        //second
        int maxRange2 = manager.chart.max(1, manager.range);
        int minRange2 = manager.chart.min(1, manager.range);

        int maxChart2 = maxStepped(maxRange2);
        float step2 = step(maxRange2);
        int minChart2 = minStepped(minRange2, step2);
        maxCurrent2 = maxRange2;
        minCurrent2 = minChart2;
        updateAxisAnimation2(minChart2, maxChart2);

        if (manager.chart.visible[1]) {
            chart.yMaxStart[1] = chart.yMaxCurrent[1];
            chart.yMaxEnd[1] = maxChart2;

            chart.yMinStart[1] = chart.yMinCurrent[1];
            chart.yMinEnd[1] = minChart2;
        }

    }

    public void updateAxisAnimation(int minChart1, int maxChart1) {
        float step = (maxChart1 - minChart1) / (float) GRID;
        if (currentMaxChart != maxChart1) {
            previousStep = currentStep;
            currentStep = step;
            resetAxisAnimation();
        }
        if (currentMaxChart != maxChart1) {
            previousMaxChart = currentMaxChart;
            currentMaxChart = maxChart1;
            resetAxisAnimation();
        }
        if (currentMinChart != minChart1) {
            previousMinChart = currentMinChart;
            currentMinChart = minChart1;
            resetAxisAnimation();
        }
    }

    public void updateAxisAnimation2(int minChart2, int maxChart2) {
        float step2 = (maxChart2 - minChart2) / (float) GRID;
        if (currentMaxChart2 != maxChart2) {
            previousStep2 = currentStep2;
            currentStep2 = step2;
            resetAxisAnimation2();
        }
        if (currentMaxChart2 != maxChart2) {
            previousMaxChart2 = currentMaxChart2;
            currentMaxChart2 = maxChart2;
            resetAxisAnimation2();
        }
        if (currentMinChart2 != minChart2) {
            previousMinChart2 = currentMinChart2;
            currentMinChart2 = minChart2;
            resetAxisAnimation2();
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

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = manager.chart.visible[id] ? 1f : 0f;
        }
    }

    @Override
    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate()
                || currentStep != previousStep || currentMaxChart != previousMaxChart || currentMinChart != previousMinChart
                || currentStep2 != previousStep2 || currentMaxChart2 != previousMaxChart2 || currentMinChart2 != previousMinChart2
        ;
        preview.needInvalidate = preview.isNeedInvalidate();
        chart.tickScale();
        chart.tickFading();
        preview.tickScale();
        preview.tickFading();
        tickAxisChange();
        tickAxisChange2();
    }

    public void tickAxisChange2() {
        if (executedAxisTime2 < durationAxis2) {
            executedAxisTime2 += ANIMATION_TICK;

            if (executedAxisTime2 > durationAxis2) {
                executedAxisTime2 = durationAxis2;
            }

            if (executedAxisTime2 == durationAxis2) {
                previousStep2 = currentStep2;
                previousMaxChart2 = currentMaxChart2;
                previousMinChart2 = currentMinChart2;
            }
        }
    }

    public float progressAxis2() {
        return Math.min(1f, executedAxisTime2 / (float) durationAxis2);
    }

    public void resetAxisAnimation2() {
        executedAxisTime2 = 0;
    }


}