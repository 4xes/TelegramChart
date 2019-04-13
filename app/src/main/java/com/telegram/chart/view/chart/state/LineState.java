package com.telegram.chart.view.chart.state;

import java.util.Arrays;

public class LineState extends State {

    public LineState(int size) {
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
    protected void endScale() {
        for (int id = 0; id < size; id++) {
            yMaxStart[id] = yMaxEnd[id];
            yMaxCurrent[id] = yMaxEnd[id];
            multiStart[id] = multiEnd[id];
            multiCurrent[id] = multiEnd[id];
        }
    }

    @Override
    protected void changeScale(float delta) {
        changeValue(yMaxStart, yMaxCurrent, yMaxEnd, delta);
        changeValue(multiStart, multiCurrent, multiEnd, delta);
    }

    public boolean isNeedInvalidate() {
        return  !(Arrays.equals(yMaxCurrent, yMaxEnd) && Arrays.equals(alphaCurrent, alphaEnd) && Arrays.equals(multiCurrent, multiEnd));
    }
}