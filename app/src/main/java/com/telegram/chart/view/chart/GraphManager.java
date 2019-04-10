package com.telegram.chart.view.chart;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.telegram.chart.data.Chart;

public class GraphManager {
    public final StateManager state;
    public final Chart chart;
    public final Range range = new Range();

    public void setVisible(int id, boolean isVisible) {
        if (state.visible[id] == isVisible) {
            return;
        }
        state.visible[id] = isVisible;
        state.setAnimationHide(id);

        invalidateById(Ids.CHART);
        invalidateById(Ids.PREVIEW);
    }

    public void update(int id, float start, float end) {
        if (this.range.start != start || this.range.end != end) {
            this.range.start = start;
            this.range.end = end;

            state.updateRange();

            invalidateById(Ids.CHART);
            if (id != Ids.RANGE) {
                invalidateById(Ids.RANGE);
            }
        }
    }

    public void invalidateById(int id) {
        if (invalidateListeners[id] != null) {
            invalidateListeners[id].needInvalidate();
        }
    }

    public int countVisible() {
        int count = 0;
        for (int id = 0; id < countLines(); id++) {
            if (state.visible[id]) {
                count++;
            }
        }
        return count;
    }

    private final InvalidateListener[] invalidateListeners = new InvalidateListener[3];

    public GraphManager(Chart chart) {
        this.chart = chart;
        this.state = new StateManager(this);
    }

    public void registerView(int id, InvalidateListener invalidateListener) {
        invalidateListeners[id] = invalidateListener;
    }

    public interface InvalidateListener {
        void needInvalidate();
        int getViewId();
    }

    public int getIndex(float x, RectF r) {
        x = x - r.left;
        if (x < r.left) {
            x = r.left;
        }
        if (x > r.right) {
            x = r.right;
        }
        int lower = chart.getLower(range.start);
        int upper =  chart.getUpper(range.end);
        int index = (int) (Math.ceil(x - (-r.width() * range.start) * getScaleRange()) / (getScaleRange() * sectionWidth(r.width())));
        if (index < lower) {
            return lower;
        }
        if (index > upper) {
            return upper;
        }
        return index;
    }

    public float getScaleRange(){
        return 1f / (range.end - range.start);
    }

    public void matrix(int id, RectF r, Matrix matrix) {
        final float width = r.width();
        final float scaleX = getScaleRange() * sectionWidth(width);
        final float scaleY = (1f / (state.chart.yMaxCurrent[id] / r.height())) * state.chart.multiCurrent[id];
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixStackedBars(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float barWidth = barWidth(width);
        final float scaleX = getScaleRange() * barWidth;
        final float scaleY = (1f / (chart.stepMax(range)/ r.height()));
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPreviewStackedBars(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = (1f / (chart.max()/ r.height()));
        final float offsetX = r.left;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPercentageBars(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = getScaleRange() * sectionWidth(width);
        final float scaleY = 1f;
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPercentagePreviewBars(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = 1f;
        final float offsetX = r.left;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPreview(int id, RectF r, Matrix matrix) {
        final float width = r.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = (1f / (state.preview.yMaxCurrent[id] / r.height()) * state.preview.multiCurrent[id]);
        final float offsetX = r.left;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void calculateLine(int index, RectF r, PointF point) {
        calculatePoint(0, index, r, point);
    }

    public void calculatePoint(int id, int index, RectF r, PointF point) {
        final float width = r.width();
        final float scaleRange = getScaleRange();
        final float scaleX = scaleRange * sectionWidth(width);
        final float dx = (-width * range.start) * scaleRange - sectionWidth(width) / 2f;
        final float offsetX = r.left + dx;
        final float scaleY = 1f / (state.chart.yMaxCurrent[id] / r.height());
        final float offsetY = r.bottom;
        point.set(index * scaleX + offsetX, -(chart.data[id].y[index] * scaleY) + offsetY);
    }

    public float sectionWidth(float width) {
        if (chart.x.length > 1) {
            return width / (chart.x.length - 1);
        }
        return width;
    }

    public float barWidth(float width) {
        if (chart.x.length > 1) {
            return width / (chart.x.length);
        }
        return width;
    }

    public int countLines() {
        return chart.data.length;
    }


    public void onTimeUpdate(long deltaTime) {
        state.tick();
        if (state.chart.needInvalidate) {
            invalidateById(Ids.CHART);
        }
        if (state.preview.needInvalidate) {
            invalidateById(Ids.PREVIEW);
        }
    }

    public static final int NONE_INDEX = -1;
}