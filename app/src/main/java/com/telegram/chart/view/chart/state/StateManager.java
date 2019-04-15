package com.telegram.chart.view.chart.state;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.DateUtils;

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

    public int prevDate1;
    public int prevDate2;
    public int currentDate1;
    public int currentDate2;

    public String prevDate = "";
    public String currentDate;

    public long executedDateTime = ANIMATION_DURATION_LONG;
    public long durationDate = ANIMATION_DURATION_LONG;


    protected final GraphManager manager;

    public StateManager(GraphManager manager) {
        this.manager = manager;
        int indexNew1 = manager.chart.getLower(manager.range.start);
        int indexNew2 = manager.chart.getUpper(manager.range.end);
        currentDate1 = indexNew1;
        currentDate2 = indexNew2;
        currentDate = DateUtils.getTitle(manager.chart.x[currentDate1] * 1000L, manager.chart.x[currentDate2] * 1000L);
    }

    public abstract void setAnimationStart();

    public void updateRange() {
        int indexNew1 = manager.chart.getLower(manager.range.start);
        int indexNew2 = manager.chart.getUpper(manager.range.end);
        if (currentDate1 != indexNew1 || currentDate2 != indexNew2) {
            prevDate1 = currentDate1;
            prevDate2 = currentDate2;
            currentDate1 = indexNew1;
            currentDate2 = indexNew2;
            prevDate = currentDate;
            currentDate = DateUtils.getTitle(manager.chart.x[currentDate1] * 1000L, manager.chart.x[currentDate2] * 1000L);
            resetDateAnimation();
        }

    }
    public abstract void updateAxisAnimation(int maxChart);
    public abstract void setAnimationHide(int targetId);

    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate()
                || currentDate1 != prevDate1 || currentDate2 != prevDate2
                || currentStep != previousStep || currentMaxChart != previousMaxChart || currentMinChart != previousMinChart;
        preview.needInvalidate = preview.isNeedInvalidate();
        chart.tickScale();
        chart.tickFading();
        preview.tickScale();
        preview.tickFading();
        tickAxisChange();
        tickDateChange();
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

    public void tickDateChange() {
        if (executedDateTime < durationDate) {
            executedDateTime += ANIMATION_TICK;

            if (executedDateTime > durationDate) {
                executedDateTime = durationDate;
            }

            if (executedDateTime == durationDate) {
                prevDate = currentDate;
                prevDate1 = currentDate1;
                prevDate2 = currentDate2;
            }
        }
    }

    public float progressDate() {
        return Math.min(1f, executedDateTime / (float) durationDate);
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

    public void resetDateAnimation() {
        executedDateTime = 0;
    }

    private final static long ANIMATION_DURATION_LONG = 300L;
}
