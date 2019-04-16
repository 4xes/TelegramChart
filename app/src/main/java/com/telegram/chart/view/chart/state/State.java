package com.telegram.chart.view.chart.state;


public abstract class State {
    public final int size;
    public long executedScaleTime = 0;
    public long durationScale = DURATION_LONG;
    public long executedFadingTime = 0;
    public long durationFading = DURATION_LONG;
    public int maxStart;
    public int maxCurrent;
    public int maxEnd;
    public final float[] alphaStart;
    public final float[] alphaCurrent;
    public final float[] alphaEnd;
    public final float[] percentStart;
    public final float[] percentCurrent;
    public final float[] percentEnd;
    public final int[] yMaxStart;
    public final int[] yMaxCurrent;
    public final int[] yMaxEnd;
    public final int[] yMinStart;
    public final int[] yMinCurrent;
    public final int[] yMinEnd;
    public final float[] multiStart;
    public final float[] multiCurrent;
    public final float[] multiEnd;
    public boolean needInvalidate = true;

    public void tickFading() {
        if (executedFadingTime < durationFading) {
            executedFadingTime += ANIMATION_TICK;

            if (executedFadingTime > durationFading) {
                executedFadingTime = durationFading;
            }

            if (executedFadingTime == durationFading) {
                endFading();
            } else {
                float delta = (float) executedFadingTime / durationFading;
                changeFading(delta);
            }
        }
    }

    protected abstract void endFading();

    protected abstract void changeFading(float delta);

    public void tickScale() {
        if (executedScaleTime < durationScale) {
            executedScaleTime += ANIMATION_TICK;

            if (executedScaleTime > durationScale) {
                executedScaleTime = durationScale;
            }

            if (executedScaleTime == durationScale) {
                for (int id = 0; id < size; id++) {
                    endScale();
                }
            } else {
                for (int id = 0; id < size; id++) {
                    float delta = (float) executedScaleTime / durationScale;
                    changeScale(delta);
                }
            }
        }
    }

    abstract void endScale();

    abstract void changeScale(float delta);

    protected void changeValue(float[] start, float[] current, float end[], float delta) {
        for (int id = 0; id < size; id++) {
            current[id] = start[id] + ((end[id] - start[id]) * delta);
            if (start[id] < end[id]) {
                current[id] = Math.min(current[id], end[id]);
            } else {
                current[id] = Math.max(current[id], end[id]);
            }
        }
    }

    protected void changeMax(float delta) {
        maxCurrent = maxStart + (int) ((maxEnd - maxStart) * delta);
        if (maxStart < maxEnd) {
            maxCurrent = Math.min(maxCurrent, maxEnd);
        } else {
            maxCurrent = Math.max(maxCurrent, maxEnd);
        }
    }

    protected void changeValue(int[] start, int[] current, int end[], float delta) {
        for (int id = 0; id < size; id++) {
            current[id] = start[id] + (int) ((end[id] - start[id]) * delta);
            if (start[id] < end[id]) {
                current[id] = Math.min(current[id], end[id]);
            } else {
                current[id] = Math.max(current[id], end[id]);
            }
        }
    }

    public State(int size) {
        this.size = size;
        alphaStart = new float[size];
        alphaCurrent = new float[size];
        alphaEnd = new float[size];
        percentStart = new float[size];
        percentCurrent = new float[size];
        percentEnd = new float[size];
        yMaxStart = new int[size];
        yMaxCurrent = new int[size];
        yMaxEnd = new int[size];
        yMinStart = new int[size];
        yMinCurrent = new int[size];
        yMinEnd = new int[size];
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

    public abstract boolean isNeedInvalidate();


    public final static long DURATION_LONG = 300L;
    public final static long DURATION_SHORT = 150L;
    public final static long ANIMATION_TICK = 16L;
}
