package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;

import static com.telegram.chart.view.chart.state.State.ANIMATION_TICK;

public abstract class StateManager {
    public long executedAxisTime = ANIMATION_DURATION_LONG;
    public long durationAxis = ANIMATION_DURATION_LONG;
    public int maxCurrent;
    public int previousMinChart = 0;
    public int previousMaxChart = 0;
    public int currentMinChart = 0;
    public int currentMaxChart = 0;
    public int minCurrent = 0;
    public float previousStep;
    public float currentStep;

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

    public State chart;
    public State preview;

    protected final GraphManager manager;

    public StateManager(GraphManager manager) {
        this.manager = manager;
    }

    public abstract void setAnimationStart();
    public abstract void updateRange();
    public abstract void updateAxisAnimation(int maxChart);
    public abstract void setAnimationHide(int targetId);

    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate() || currentStep != previousStep || currentMaxChart != previousMaxChart || currentMinChart != previousMinChart;
        preview.needInvalidate = preview.isNeedInvalidate();
        chart.tickScale();
        chart.tickFading();
        preview.tickScale();
        preview.tickFading();
        tickAxisChange();
    }

    public void tickAxisChange() {
        if (executedAxisTime < durationAxis) {
            executedAxisTime += ANIMATION_TICK;

            if (executedAxisTime > durationAxis) {
                executedAxisTime = durationAxis;
            }

            if (executedAxisTime == durationAxis) {
                previousStep = currentStep;
                previousMaxChart = currentMaxChart;
                previousMinChart = currentMinChart;
            }
        }
    }

    public float progressAxis() {
        return Math.min(1f, executedAxisTime / (float) durationAxis);
    }

    public float progressAxis2() {
        return Math.min(1f, executedAxisTime2 / (float) durationAxis2);
    }

    public void resetAxisAnimation() {
        executedAxisTime = 0;
    }

    private final static long ANIMATION_DURATION_LONG = 300L;
}
