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

        long maxPreview = getMaxPreview();
        long maxChart = getMaxChartStepped();
        long newCurrent = getMaxChart();
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

    public long getMaxPreview() {
        long max = Long.MIN_VALUE;

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

    public long getMaxChart() {
        long max = Long.MIN_VALUE;

        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                final LineData line = graph.lines[id];
                final long maxLine = line.getMaxY(graph.range.start, graph.range.end);
                if (max < maxLine) {
                    max = maxLine;
                }
            }
        }
        return max;
    }

    public long getMaxChartStepped() {
        return toMaxChartStepped(getMaxChart());
    }

    public static long toMaxChartStepped(long maxChart) {
        long max = maxChart;
        if (max != Long.MIN_VALUE) {
            float step = XYRender.calculateStep(0, max, XYRender.GRID);
            max = (int) Math.floor(step * (XYRender.GRID));
        }
        return max;
    }

    public static float step(long maxChart) {
        return XYRender.calculateStep(0, maxChart, XYRender.GRID);
    }

    public long getMaxChartStepped(int id) {
        long max = graph.lines[id].getMaxY(graph.range.start, graph.range.end);
        if (max != Long.MIN_VALUE) {
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

        long newCurrent = getMaxChart();
        updateCurrentAnimation(newCurrent);

        long maxChart = toMaxChartStepped(newCurrent);
        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxChart;
            }
        }

    }


    public void updateCurrentAnimation(long maxChart) {
        float step = step(maxChart);
        if (currentStep != step) {
            previousStep = currentStep;
            currentStep = step;
            previousMaxY = currentMaxY;
            currentMaxY = maxChart;
            resetYAnimation();
        }
    }

    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        long maxPreview = getMaxPreview();
        long maxChart = getMaxChartStepped();

        if (maxPreview == Long.MIN_VALUE) {
            maxPreview = graph.lines[targetId].getMaxY();
        }

        if (maxChart== Long.MIN_VALUE) {
            maxChart = getMaxChartStepped(targetId);
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

            if ((targetId != id || getMaxChartStepped(id) == maxChart)) {
                if (visible[id]) {
                    chart.yMaxStart[id] = chart.yMaxCurrent[id];
                    chart.yMaxEnd[id] = maxChart;
                }
            }
        }

        chart.yMaxStart[targetId] = chart.yMaxCurrent[targetId];
        chart.yMaxEnd[targetId] = visible[targetId] ? maxChart : maxChart / 4L;


        long newCurrent = getMaxChart();
        updateCurrentAnimation(newCurrent);
    }


    public State getChart() {
        return chart;
    }

    public void setChart(State chart) {
        this.chart = chart;
    }

    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate() || currentStep != previousStep ;
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

    private final static long ANIMATION_DURATION_LONG = 300L;
    private final static long ANIMATION_DURATION_SHORT = 100L;
    private final static long ANIMATION_TICK = 16L;

    public class State {
        private final int size;
        public final long[] yMaxStart;
        public final long[] yMaxCurrent;
        public final long[] yMaxEnd;
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
                        yMaxCurrent[id] = yMaxStart[id] + (long) ((yMaxEnd[id] - yMaxStart[id]) * delta);
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
            yMaxStart = new long[size];
            yMaxCurrent = new long[size];
            yMaxEnd = new long[size];
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