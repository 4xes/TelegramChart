package com.telegram.chart.view.chart.state;

import java.util.Arrays;

public class StackedState extends State {

    public StackedState(int size) {
        super(size);
    }

    @Override
    protected void endFading() {
        for (int id = 0; id < size; id++) {
            alphaStart[id] = alphaEnd[id];
            alphaCurrent[id] = alphaEnd[id];
        }
    }

    @Override
    protected void changeFading(float delta) {
        changeValue(alphaStart, alphaCurrent, alphaEnd, delta);
    }

    @Override
    void endScale() {
        maxStart = maxEnd;
        maxCurrent = maxEnd;
        for (int id = 0; id < size; id++) {
            percentStart[id] = percentEnd[id];
            percentCurrent[id] = percentEnd[id];
            multiStart[id] = multiEnd[id];
            multiCurrent[id] = multiEnd[id];
        }
    }

    @Override
    void changeScale(float delta) {
        changeMax(delta);
        changeValue(multiStart, multiCurrent, multiEnd, delta);
        changeValue(percentStart, percentCurrent, percentEnd, delta);
    }

    public boolean isNeedInvalidate() {
        return !(Arrays.equals(percentCurrent, percentEnd) && Arrays.equals(alphaCurrent, alphaEnd) && Arrays.equals(multiCurrent, multiEnd) && maxCurrent == maxEnd);
    }
}
