package com.telegram.chart.view.chart;

import android.util.Log;

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

        for (int id = 0; id < graph.countLines(); id++) {
            preview.yMaxStart[id] = graph.maxY;
            preview.yMaxCurrent[id] = preview.yMaxStart[id];
            preview.yMaxEnd[id] = graph.maxY;

            preview.alphaStart[id] = 1f;
            preview.alphaCurrent[id] = preview.alphaStart[id];
            preview.alphaEnd[id] = 1f;

            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 0f;

            chart.alphaStart[id] = 1f;
            chart.alphaCurrent[id] = chart.alphaStart[id];
            chart.alphaEnd[id] = 1f;

            chart.yMaxStart[id] = graph.maxY;
            chart.yMaxCurrent[id] = chart.yMaxStart[id];
            chart.yMaxEnd[id] = graph.maxY;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 0f;
        }
        setAnimationStart();
    }

    public void setAnimationStart() {
        preview.executedTime = 0;
        preview.duration = ANIMATION_DURATION;
        chart.executedTime = 0;
        chart.duration = ANIMATION_DURATION;

        for (int id = 0; id < graph.countLines(); id++) {
            preview.multiStart[id] = 0f;
            preview.multiCurrent[id] = preview.multiStart[id];
            preview.multiEnd[id] = 1f;

            chart.multiStart[id] = 0f;
            chart.multiCurrent[id] = chart.multiStart[id];
            chart.multiEnd[id] = 1f;
        }
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
           // Log.d("animation", "dt: " + deltaTime);
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
                    if (alphaCurrent[id] < 0f) {
                        alphaCurrent[id] = 0f;
                    }
                    if (alphaCurrent[id] > 1f) {
                        alphaCurrent[id] = 1f;
                    }

                    multiCurrent[id] = multiStart[id] + ((multiEnd[id] - multiStart[id]) * delta);
                    if (multiCurrent[id] < 0f) {
                        multiCurrent[id] = 0f;
                    }
                    if (multiCurrent[id] > 1f) {
                        multiCurrent[id] = 1f;
                    }
                }

                if (executedTime == ANIMATION_DURATION) {
                    for (int id = 0; id < size; id++) {
                        yMaxCurrent[id] = yMaxEnd[id];
                        alphaCurrent[id] = alphaEnd[id];
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

        public boolean isNeedInvalidate() {
            return !(Arrays.equals(yMaxCurrent, yMaxEnd) && Arrays.equals(alphaCurrent, alphaEnd) && Arrays.equals(multiCurrent, multiEnd));
        }
    }
}