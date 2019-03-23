package com.telegram.chart.view.chart;

import android.graphics.PointF;
import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.DateUtils;

import java.util.ArrayList;

public class Graph {
    public final StateManager state;
    public final LineData[] lines;
    public final long[] dates;
    public Range range = new Range();

    public long[] getY(int id) {
        return lines[id].getY();
    }

    public String getName(int index) {
        return lines[index].getName();
    }

    public long getDate(int index) {
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
        for (InvalidateListener listener: invalidateListeners) {
            if (listener.getViewId() != Ids.RANGE) {
                listener.needInvalidate();
            }
        }
    }

    public String getInfoDate(int index) {
        return DateUtils.getInfoDate(getDate(index));
    }

    public String getValue(int id, int index) {
        return String.valueOf(lines[id].getY(index));
    }

    public void update(int id, float start, float end) {
        if (this.range.start != start || this.range.end != end) {
            this.range.start = start;
            this.range.end = end;

            state.updateRange();
            for (InvalidateListener listener: invalidateListeners) {
                if (listener.getViewId() == Ids.CHART || (id != Ids.RANGE && listener.getViewId() == Ids.RANGE)) {
                    listener.needInvalidate();
                }
            }
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

    private final ArrayList<InvalidateListener> invalidateListeners = new ArrayList<>();

    public Graph(ChartData chartData) {
        this.lines = chartData.getLines();
        this.dates = chartData.getX();
        this.state = new StateManager(this);
    }

    public void addListener(InvalidateListener invalidateListener) {
        if (invalidateListener != null) {
            invalidateListeners.add(invalidateListener);
        }
    }

    public void removeListener(InvalidateListener invalidateListener) {
        invalidateListeners.remove(invalidateListener);
    }

    public interface InvalidateListener {
        void needInvalidate();
        int getViewId();
    }

    public int getIndex(float touchX, Bound bound) {
        float x = touchX;
        if (x < bound.left) {
            x = bound.left;
        }
        if (x > bound.right) {
            x = bound.right;
        }
        final float dx = (x - bound.left);
        if (countVisible() > 1) {
            float percent = (range.start) + (dx) / bound.width() * (range.end - range.start);
            final int maxIndex = dates.length - 1;
            int index = (int) (percent * (maxIndex + 1));
            if (index > maxIndex) {
                return maxIndex;
            }
            return index;
        } else {
            return -1;
        }
    }

    public float getScaleRange(){
        return 1f / (range.end - range.start);
    }

    public void matrix(int id, Bound bound, float[] matrix) {
        final float scaleX = getScaleRange() * sectionWidth(bound.width());
        final float scaleY = (1f / (state.chart.yMaxCurrent[id] / bound.height())) * state.chart.multiCurrent[id];
        final float dx = (-bound.width() * range.start) * getScaleRange();
        final float offsetX = bound.left + dx + bound.offsetX;
        final float offsetY = bound.bottom;
        matrix[0] = scaleX;
        matrix[1] = scaleY;
        matrix[2] = offsetX;
        matrix[3] = offsetY;
    }

    public void getMaxLength() {

    }

    public void matrixPreview(int id, Bound bound, float[] matrix) {
        final float width = bound.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = (1f / (state.preview.yMaxCurrent[id] / bound.height()) * state.preview.multiCurrent[id]);
        final float offsetX = bound.left + bound.offsetX;
        final float offsetY = bound.bottom;
        matrix[0] = scaleX;
        matrix[1] = scaleY;
        matrix[2] = offsetX;
        matrix[3] = offsetY;
    }

    public void valuesY(Bound bound, float[] values) {
        final float max = state.getMaxChart();
        final float scaleY = 1f / (max / bound.height());
        final float offsetY = bound.bottom;
        values[0] = max;
        values[1] = scaleY;
        values[2] = offsetY;
    }

    public void calculateLine(int index, Bound bound, PointF point) {
        calculatePoint(0, index, bound, point);
    }

    public void calculatePoint(int id, int index, Bound bound, PointF point) {
        final float width = bound.width();
        final float scaleRange = getScaleRange();
        final float scaleX = scaleRange * sectionWidth(width);
        final float dx = (-width * range.start) * scaleRange;
        final float offsetX = bound.left + dx + bound.offsetX;
        final float scaleY = 1f / (state.chart.yMaxCurrent[id] / bound.height());
        final float offsetY = bound.bottom;
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
        for (InvalidateListener listener: invalidateListeners) {
            if ((listener.getViewId() == Ids.CHART && state.chart.needInvalidate) || (listener.getViewId() == Ids.PREVIEW && state.preview.needInvalidate)) {
                listener.needInvalidate();
            }
        }
    }

    public static final int NONE_INDEX = -1;
}