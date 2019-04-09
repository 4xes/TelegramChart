package com.telegram.chart.view.chart;

import java.util.Arrays;

import static com.telegram.chart.data.Chart.step;
import static com.telegram.chart.data.Chart.toStepped;

public class StateManager {

    private final GraphManager manager;
    public State chart;
    public State preview;
    public boolean[] visible;
    public long executedYTime = ANIMATION_DURATION_LONG;


    public long durationY = ANIMATION_DURATION_LONG;
    public long previousMaxY;
    public long currentMaxY;
    public float previousStep;
    public float currentStep;

    public StateManager(GraphManager manager) {
        this.manager = manager;
        this.chart = new State(manager.countLines());
        this.preview = new State(manager.countLines());
        this.visible = new boolean[manager.countLines()];
        Arrays.fill(visible, true);

        int maxPreview = manager.chart.max();
        int newCurrent = manager.chart.max(manager.range);
        int maxChart = toStepped(newCurrent);
        float step = step(newCurrent);

        previousMaxY = newCurrent;
        currentMaxY = previousMaxY;
        previousStep = step;
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

    public void updateRange() {
        chart.resetScaleAnimation(ANIMATION_DURATION_SHORT);

        int maxRange = manager.chart.max(manager.range);
        updateCurrentAnimation(maxRange);

        int maxStepped = toStepped(maxRange);
        for (int id = 0; id < manager.countLines(); id++) {
            if (visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxStepped;
            }
        }

    }


    public void updateCurrentAnimation(int maxChart) {
        float step = step(maxChart);
        if (currentStep != step) {
            previousStep = currentStep;
            currentStep = step;
            previousMaxY = currentMaxY;
            currentMaxY = maxChart;
            resetYAnimation();
        }
    }

    private final static long ANIMATION_DURATION_LONG = 300L;
    private final static long ANIMATION_DURATION_SHORT = 150L;

    public State getChart() {
        return chart;
    }

    public void setChart(State chart) {
        this.chart = chart;
    }

    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate() || currentStep != previousStep;
        preview.needInvalidate = preview.isNeedInvalidate();
        chart.tickScale();
        chart.tickFading();
        preview.tickScale();
        preview.tickFading();
        tickYChange();
    }

    public void tickYChange() {
        if (executedYTime < durationY) {
            executedYTime += ANIMATION_TICK;

            if (executedYTime > durationY) {
                executedYTime = durationY;
            }

            if (executedYTime == durationY) {
                previousStep = currentStep;
            }
        }
    }

    public float progressY() {
        return Math.min(1f, executedYTime / (float) durationY);
    }

    public void resetYAnimation() {
        executedYTime = 0;
    }

    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        int max = manager.chart.max();
        int maxRange = manager.chart.max(manager.range);
        int maxStepped = toStepped(maxRange);
        if (max == Integer.MIN_VALUE) {
            max = manager.chart.data[targetId].max;
        }

        int targetMaxStepped = manager.chart.stepMax(targetId, manager.range);
        if (maxStepped == Integer.MIN_VALUE) {
            maxStepped = targetMaxStepped;
        }

        for (int id = 0; id < manager.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = visible[id] ? 1f : 0f;

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
            chart.yMaxEnd[targetId] = visible[targetId] ? maxStepped : maxStepped / 4;
        }

        updateCurrentAnimation(maxRange);
    }
    private final static long ANIMATION_TICK = 16L;

    public class State {
        private final int size;
        public final int[] yMaxStart;
        public final int[] yMaxCurrent;
        public final int[] yMaxEnd;
        public final float[] multiStart;
        public final float[] multiCurrent;
        public final float[] multiEnd;
        public final float[] alphaStart;
        public final float[] alphaCurrent;
        public final float[] alphaEnd;
        public long executedScaleTime = 0;
        public long durationScale = ANIMATION_DURATION_LONG;
        public long executedFadingTime = 0;
        public long durationFading = ANIMATION_DURATION_LONG;
        public boolean needInvalidate = true;

        public void tickFading() {
            if (executedFadingTime < durationFading) {
                executedFadingTime += ANIMATION_TICK;

                if (executedFadingTime > durationFading) {
                    executedFadingTime = durationFading;
                }

                if (executedFadingTime == durationFading) {
                    for (int id = 0; id < size; id++) {
                        alphaStart[id] = alphaEnd[id];
                        alphaCurrent[id] = alphaEnd[id];
                    }
                } else {
                    for (int id = 0; id < size; id++) {
                        float delta = (float) executedFadingTime / durationFading;
                        alphaCurrent[id] = alphaStart[id] + ((alphaEnd[id] - alphaStart[id]) * delta);
                        if (alphaStart[id] < alphaEnd[id]) {
                            alphaCurrent[id] = Math.min(alphaCurrent[id], alphaEnd[id]);
                        } else {
                            alphaCurrent[id] = Math.max(alphaCurrent[id], alphaEnd[id]);
                        }
                    }
                }
            }
        }


        public void tickScale() {
            if (executedScaleTime < durationScale) {
                executedScaleTime += ANIMATION_TICK;

                if (executedScaleTime > durationScale) {
                    executedScaleTime = durationScale;
                }

                if (executedScaleTime == durationScale) {
                    for (int id = 0; id < size; id++) {
                        yMaxStart[id] = yMaxEnd[id];
                        yMaxCurrent[id] = yMaxEnd[id];
                        multiStart[id] = multiEnd[id];
                        multiCurrent[id] = multiEnd[id];
                    }
                } else {
                    for (int id = 0; id < size; id++) {
                        float delta = (float) executedScaleTime / durationScale;
                        yMaxCurrent[id] = yMaxStart[id] + (int) ((yMaxEnd[id] - yMaxStart[id]) * delta);
                        if (yMaxStart[id] < yMaxEnd[id]) {
                            yMaxCurrent[id] = Math.min(yMaxCurrent[id], yMaxEnd[id]);
                        } else {
                            yMaxCurrent[id] = Math.max(yMaxCurrent[id], yMaxEnd[id]);
                        }

                        multiCurrent[id] = multiStart[id] + ((multiEnd[id] - multiStart[id]) * delta);
                        if (multiStart[id] < multiEnd[id]) {
                            multiCurrent[id] = Math.min(multiCurrent[id], multiEnd[id]);
                        } else {
                            multiCurrent[id] = Math.max(multiCurrent[id], multiEnd[id]);
                        }
                    }
                }
            }
        }


        public State(int countLines) {
            size = countLines;
            yMaxStart = new int[size];
            yMaxCurrent = new int[size];
            yMaxEnd = new int[size];
            alphaStart = new float[size];
            alphaCurrent = new float[size];
            alphaEnd = new float[size];
            multiStart = new float[size];
            multiCurrent = new float[size];
            multiEnd = new float[size];
        }

        public void resetScaleAnimation(long newDuration) {
            executedScaleTime = 0;
            durationScale = newDuration;
        }

        public void resetFadingAnimation(long newDuration) {
            executedFadingTime = 0;
            durationFading = newDuration;
        }

        public boolean isNeedInvalidate() {
            return !(Arrays.equals(yMaxCurrent, yMaxEnd) && Arrays.equals(alphaCurrent, alphaEnd) && Arrays.equals(multiCurrent, multiEnd));
        }
    }
}