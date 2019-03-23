package com.telegram.chart.view.chart;

import android.util.Log;

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

        long max = getMaxY();
        for (int id = 0; id < graph.countLines(); id++) {
            preview.yMaxStart[id] = max;
            preview.yMaxCurrent[id] = preview.yMaxStart[id];
            preview.yMaxEnd[id] = max;

            preview.alphaStart[id] = 1f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = 1f;

            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.alphaStart[id] = 1f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = 1f;

            chart.yMaxStart[id] = max;
            chart.yMaxCurrent[id] = chart.yMaxStart[id];
            chart.yMaxEnd[id] = max;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }
        setAnimationStart();
    }


    public void setAnimationStart() {
        resetTimeAnimation();

        for (int id = 0; id < graph.countLines(); id++) {
            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }
    }

    public long getMaxY() {
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

    public void setAnimationHide(int targetId) {
        resetTimeAnimation();

        long max = getMaxY();

        if (max == Long.MIN_VALUE) {
            max = graph.lines[targetId].getMaxY();
        }

        for (int id = 0; id < graph.countLines(); id++) {
            preview.alphaStart[id] = chart.alphaCurrent[id];
            preview.alphaEnd[id] = visible[id] ? 1f : 0f;

            chart.alphaStart[id] = chart.alphaCurrent[id];
            chart.alphaEnd[id] = visible[id] ? 1f : 0f;

            if ((targetId != id || graph.lines[targetId].getMaxY() == max)) {
                preview.yMaxStart[id] = preview.yMaxCurrent[id];
                preview.yMaxEnd[id] = max;

                chart.yMaxStart[id] = chart.yMaxCurrent[id];
                chart.yMaxEnd[id] = max;

                if (graph.lines[targetId].getMaxY() == max) {
                    preview.yMaxStart[id] = max;
                    preview.yMaxCurrent[id] = max;
                    preview.yMaxEnd[id] = max;
                }
            }
        }

    }

    public void resetTimeAnimation() {
        preview.executedTime = 0;
        chart.executedTime = 0;
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
        chart.tick();
        preview.tick();
    }

    private final static long ANIMATION_DURATION = 400L;

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
        public long executedTime = 0;
        public long duration = ANIMATION_DURATION;
        public boolean needInvalidate = true;

        public void tick() {
            if (executedTime < duration) {
                executedTime += 16;

                if (executedTime > duration) {
                    executedTime = ANIMATION_DURATION;
                }

                float delta = (float) executedTime / ANIMATION_DURATION;

                for (int id = 0; id < size; id++) {
                    yMaxCurrent[id] = yMaxStart[id] + (long) ((yMaxEnd[id] - yMaxStart[id]) * delta);
                    if (yMaxStart[id] < yMaxEnd[id]) {
                        yMaxCurrent[id] = Math.min(yMaxCurrent[id], yMaxEnd[id]);
                    } else {
                        yMaxCurrent[id] = Math.max(yMaxCurrent[id], yMaxEnd[id]);
                    }

                    alphaCurrent[id] = alphaStart[id] + ((alphaEnd[id] - alphaStart[id]) * delta);
                    if (alphaStart[id] < alphaEnd[id]) {
                        alphaCurrent[id] = Math.min(alphaCurrent[id], alphaEnd[id]);
                    } else {
                        alphaCurrent[id] = Math.max(alphaCurrent[id], alphaEnd[id]);
                    }

                    multiCurrent[id] = multiStart[id] + ((multiEnd[id] - multiStart[id]) * delta);
                    if (multiStart[id] < multiEnd[id]) {
                        multiCurrent[id] = Math.min(multiCurrent[id], multiEnd[id]);
                    } else {
                        multiCurrent[id] = Math.max(multiCurrent[id], multiEnd[id]);
                    }
                }

                if (executedTime == ANIMATION_DURATION) {
                    for (int id = 0; id < size; id++) {
                        endAnimation();
                    }
                }
            }
        }

        private void endAnimation() {
            for (int id = 0; id < size; id++) {
                yMaxStart[id] = yMaxEnd[id];
                yMaxCurrent[id] = yMaxEnd[id];
                alphaStart[id] = alphaEnd[id];
                alphaCurrent[id] = alphaEnd[id];
                multiStart[id] = multiEnd[id];
                multiCurrent[id] = multiEnd[id];
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

        public boolean isNeedInvalidate() {
            return !(Arrays.equals(yMaxCurrent, yMaxEnd) && Arrays.equals(alphaCurrent, alphaEnd) && Arrays.equals(multiCurrent, multiEnd));
        }
    }
}