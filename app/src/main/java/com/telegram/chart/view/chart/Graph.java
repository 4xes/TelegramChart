package com.telegram.chart.view.chart;

import android.graphics.PointF;
import android.graphics.RectF;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.DateUtils;

import java.util.ArrayList;

public class Graph {
    public final StateManager state;
    public final LineData[] lines;
    public final int[] dates;
    public Range range = new Range();

    public int[] getY(int id) {
        return lines[id].getY();
    }

    public String getName(int index) {
        return lines[index].getName();
    }

    public int getDate(int index) {
        return dates[index];
    }

    public int getColor(int index) {
        return lines[index].getColor();
    }

    public void setVisible(int id, boolean isVisible) {
        if (state.visible[id] == isVisible) {
            return;
        }
        state.visible[id] = isVisible;
        state.setAnimationHide(id);

        invalidateById(Ids.CHART);
        invalidateById(Ids.PREVIEW);
    }

    public String getInfoDate(int index) {
        return DateUtils.getInfoDate(getDate(index) * 1000L);
    }

    public String getXDate(int index) {
        return DateUtils.getDateX(getDate(index) * 1000L);
    }

    public String getValue(int id, int index) {
        return String.valueOf(lines[id].getY(index));
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

    public Graph(ChartData chartData) {
        this.lines = chartData.getLines();
        this.dates = chartData.getX();
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
        int lower = LineData.getLowerIndex(range.start, dates.length - 1);
        int upper =  LineData.getUpperIndex(range.end, dates.length - 1);
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

    public void matrix(int id, RectF r, float[] matrix) {
        final float scaleX = getScaleRange() * sectionWidth(r.width());
        final float scaleY = (1f / (state.chart.yMaxCurrent[id] / r.height())) * state.chart.multiCurrent[id];
        final float dx = (-r.width() * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom;
        matrix[0] = scaleX;
        matrix[1] = scaleY;
        matrix[2] = offsetX;
        matrix[3] = offsetY;
    }

    public void matrixPreview(int id, RectF r, float[] matrix) {
        final float width = r.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = (1f / (state.preview.yMaxCurrent[id] / r.height()) * state.preview.multiCurrent[id]);
        final float offsetX = r.left;
        final float offsetY = r.bottom;
        matrix[0] = scaleX;
        matrix[1] = scaleY;
        matrix[2] = offsetX;
        matrix[3] = offsetY;
    }

    public void calculateLine(int index, RectF r, PointF point) {
        calculatePoint(0, index, r, point);
    }

    public void calculatePoint(int id, int index, RectF r, PointF point) {
        final float width = r.width();
        final float scaleRange = getScaleRange();
        final float scaleX = scaleRange * sectionWidth(width);
        final float dx = (-width * range.start) * scaleRange;
        final float offsetX = r.left + dx;
        final float scaleY = 1f / (state.chart.yMaxCurrent[id] / r.height());
        final float offsetY = r.bottom;
        point.set(index * scaleX + offsetX, -(getY(id)[index] * scaleY) + offsetY);
    }

    public float sectionWidth(float width) {
        if (dates.length > 1) {
            return width / (dates.length - 1);
        }
        return width;
    }

    public int countLines() {
        return lines.length;
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