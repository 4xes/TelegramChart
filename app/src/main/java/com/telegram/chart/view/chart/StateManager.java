package com.telegram.chart.view.chart;

import com.telegram.chart.data.LineData;

import java.util.Arrays;

public class StateManager {

    private final Graph graph;
    public State chart;
    public State preview;
    public boolean[] visible;
    public long executedYTime = ANIMATION_DURATION_LONG;


    public long durationY = ANIMATION_DURATION_LONG;
    public long previousMaxY;
    public long currentMaxY;
    public float previousStep;
    public float currentStep;

    public StateManager(Graph graph) {
        this.graph = graph;
        this.chart = new State(graph.countLines());
        this.preview = new State(graph.countLines());
        this.visible = new boolean[graph.countLines()];
        Arrays.fill(visible, true);

        int maxPreview = getMaxPreview();
        int maxChart = getMaxChartStepped();
        int newCurrent = getMaxChart();
        float step = step(newCurrent);

        previousMaxY = newCurrent;
        currentMaxY = previousMaxY;
        previousStep = step;
        currentStep = previousStep;

        for (int id = 0; id < graph.countLines(); id++) {
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

    public int getMaxPreview() {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                final LineData line = graph.lines[id];
                if (max < line.getMaxY()) {
                    max = line.getMaxY();
                }
            }
        }
        return max;
    }

    public int getMaxChart() {
        int max = Integer.MIN_VALUE;

        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                final LineData line = graph.lines[id];
                final int maxLine = line.getMaxY(graph.range.start, graph.range.end);
                if (max < maxLine) {
                    max = maxLine;
                }
            }
        }
        return max;
    }

    public int getMaxChartStepped() {
        return toMaxChartStepped(getMaxChart());
    }

    public static int toMaxChartStepped(int maxChart) {
        int max = maxChart;
        if (max != Integer.MIN_VALUE) {
            float step = XYRender.calculateStep(0, max, XYRender.GRID);
            max = (int) Math.floor(step * (XYRender.GRID));
        }
        return max;
    }

    public static float step(long maxChart) {
        return XYRender.calculateStep(0, maxChart, XYRender.GRID);
    }

    public int getMaxChartStepped(int id) {
        int max = graph.lines[id].getMaxY(graph.range.start, graph.range.end);
        if (max != Integer.MIN_VALUE) {
            float step = XYRender.calculateStep(0, max, XYRender.GRID);
            max = (int) Math.floor(step * (XYRender.GRID));
        }
        return max;
    }



    public void setAnimationStart() {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);

        for (int id = 0; id < graph.countLines(); id++) {
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

        int newCurrent = getMaxChart();
        updateCurrentAnimation(newCurrent);

        int maxChart = toMaxChartStepped(newCurrent);
        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxChart;
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

        int maxPreview = getMaxPreview();
        int maxStepped = getMaxChartStepped();
        if (maxPreview == Integer.MIN_VALUE) {
            maxPreview = graph.lines[targetId].getMaxY();
        }

        int targetMaxStepped = getMaxChartStepped(targetId);
        if (maxStepped == Integer.MIN_VALUE) {
            maxStepped = targetMaxStepped;
        }

        for (int id = 0; id < graph.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = visible[id] ? 1f : 0f;

            if ((targetId != id || graph.lines[targetId].getMaxY() == maxPreview)) {
                preview.yMaxStart[id] = preview.yMaxCurrent[id];
                preview.yMaxEnd[id] = maxPreview;

                if (graph.lines[targetId].getMaxY() == maxPreview && targetId == id) {
                    preview.yMaxStart[id] = maxPreview;
                    preview.yMaxCurrent[id] = maxPreview;
                    preview.yMaxEnd[id] = maxPreview;
                }
            }

            chart.yMaxStart[id] = chart.yMaxCurrent[id];
            chart.yMaxEnd[id] = maxStepped;
        }

        if (targetMaxStepped == maxStepped) {
            chart.yMaxStart[targetId] = chart.yMaxCurrent[targetId];
            chart.yMaxEnd[targetId] = visible[targetId] ? maxStepped : maxStepped / 4;
        }

        int newCurrent = getMaxChart();
        updateCurrentAnimation(newCurrent);
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