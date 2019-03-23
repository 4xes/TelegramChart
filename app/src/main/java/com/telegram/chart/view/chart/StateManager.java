package com.telegram.chart.view.chart;

import com.telegram.chart.data.LineData;

import java.util.Arrays;

public class StateManager {

    private final Graph graph;
    public State chart;
    public State preview;
    public boolean[] visible;

    public StateManager(Graph graph) {
        this.graph = graph;
        this.chart = new State(graph.countLines());
        this.preview = new State(graph.countLines());
        this.visible = new boolean[graph.countLines()];
        Arrays.fill(visible, true);

        long maxPreview = getMaxPreview();
        long maxChart = getMaxChart();
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

    public long getMaxChart(int id) {
        return graph.lines[id].getMaxY(graph.range.start, graph.range.end);
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

        long maxChart = getMaxChart();
        for (int id = 0; id < graph.countLines(); id++) {
            if (visible[id]) {
                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = maxChart;
            }
        }

    }

    public void setAnimationHide(int targetId) {
        chart.resetScaleAnimation(ANIMATION_DURATION_LONG);
        chart.resetFadingAnimation(ANIMATION_DURATION_LONG);
        preview.resetScaleAnimation(ANIMATION_DURATION_LONG);
        preview.resetFadingAnimation(ANIMATION_DURATION_LONG);

        long maxPreview = getMaxPreview();
        long maxChart = getMaxChart();

        if (maxPreview == Long.MIN_VALUE) {
            maxPreview = graph.lines[targetId].getMaxY();
        }

        if (maxChart== Long.MIN_VALUE) {
            maxChart = getMaxChart(targetId);
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

            if ((targetId != id || getMaxChart(id) == maxChart)) {
                if (visible[id]) {
                    chart.yMaxStart[id] = chart.yMaxCurrent[id];
                    chart.yMaxEnd[id] = maxChart;
                }
            }
        }

        chart.yMaxStart[targetId] = chart.yMaxCurrent[targetId];
        chart.yMaxEnd[targetId] = visible[targetId] ? maxChart : maxChart / 2L;
    }


    public State getChart() {
        return chart;
    }

    public void setChart(State chart) {
        this.chart = chart;
    }

    public void tick() {
        chart.needInvalidate = chart.isNeedInvalidate();
        preview.needInvalidate = preview.isNeedInvalidate();
        chart.tickScale();
        chart.tickFading();
        preview.tickScale();
        preview.tickFading();
    }

    private final static long ANIMATION_DURATION_LONG = 300L;
    private final static long ANIMATION_DURATION_SHORT = 100L;

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
                executedFadingTime += 16;

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
                executedScaleTime += 16;

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