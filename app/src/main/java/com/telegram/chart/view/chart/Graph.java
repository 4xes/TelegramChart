package com.telegram.chart.view.chart;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    private final LineData[] lines;
    private final int[] offsetIndexes;
    private final boolean[] visible;
    private int maxCount;
    private final long[] x;
    private long maxY;
    private long minY;
    private Range range = new Range();

    public int maxPoints() {
        int maxSize = 0;
        for (LineData line : lines) {
            maxSize = Math.max(maxSize, 4 + (line.size() - 2) * 4);
        }
        return maxSize;
    }

    public LineData[] getLines() {
        return lines;
    }

    public long[] getY(int index) {
        return lines[index].getY();
    }

    public String getName(int index) {
        return lines[index].getName();
    }

    public long[] getX() {
        return x;
    }

    public long getX(int num) {
        return x[num];
    }

    public long getMaxY() {
        return maxY;
    }

    public long getMinY() {
        return minY;
    }

    public int getColor(int index) {
        return lines[index].getColor();
    }

    public void setVisible(int id, boolean isVisible) {
        visible[id] = isVisible;
        notifyInvalidate();
    }

    public String getInfoDate(int index) {
        return DateUtils.getInfoDate(getX(index));
    }

    public String getValue(int id, int index) {
        return String.valueOf(lines[id].getY(index));
    }

    public long getMaxY(int index) {
        return lines[index].getMaxY();
    }

    public long getMinY(int index) {
        return lines[index].getMinY();
    }

    public float getStart() {
        return range.start;
    }

    public void setStart(float start) {
        if (this.range.start != start) {
            this.range.start = start;
        }
        notifyInvalidate();
    }

    public void setStartAndMin(float start, float end) {
        if (this.range.start != start || this.range.end != end) {
            this.range.start = start;
            this.range.end = end;
            notifyInvalidate();
        }
    }

    public float getEnd() {
        return range.end;
    }

    public float getMin() {
        return range.min;
    }

    public void setEnd(float end) {
        if (this.range.end != end) {
            this.range.end = end;
        }
        notifyInvalidate();

    }

    public interface DataChangeListener {
        void onChange();
    }

    public boolean isVisible(int index) {
        return visible[index];
    }

    public int countVisible() {
        int count = 0;
        for (int id = 0; id < size(); id++) {
            if (isVisible(id)) {
                count++;
            }
        }
        return count;
    }

    private final ArrayList<InvalidateListener> invalidateListeners = new ArrayList<>();

    public Graph(ChartData chartData) {
        this.lines = chartData.getLines();
        this.x = chartData.getX();
        visible = new boolean[lines.length];
        maxCount = 0;
        for (int i = 0; i < lines.length; i++) {
            visible[i] = true;
            if (maxCount < lines[i].size()) {
                maxCount = lines[i].size();
            }
        }
        offsetIndexes = new int[lines.length];
        for (int i = 0; i < lines.length; i++) {
            offsetIndexes[i] = maxCount - lines[i].size();
        }
        this.maxY = chartData.getMaxY();
        this.minY = chartData.getMinY();
    }

    public void addListener(InvalidateListener invalidateListener) {
        if (invalidateListener != null) {
            invalidateListeners.add(invalidateListener);
        }
    }

    public void removeListener(InvalidateListener invalidateListener) {
        invalidateListeners.remove(invalidateListener);
    }

    public void notifyInvalidate() {
        for (InvalidateListener listener: invalidateListeners) {
            listener.needInvalidate();
        }
    }

    public interface InvalidateListener {
        void needInvalidate();
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
        if (maxCount > 1) {
            float percent = (range.start) + (dx) / bound.width() * (range.end - range.start);
            final int maxIndex = maxCount - 1;
            int index = (int) (percent * (maxIndex + 1));
            if (index > maxIndex) {
                return maxIndex;
            }
            return index;
        } else {
            if (maxCount == 1) {
                return 0;
            }
            return -1;
        }
    }

    public float getScaleRange(){
        return 1f / (range.end - range.start);
    }

    public void recalculateLines(int id, Bound bound, float points[]) {
        long[] y = getY(id);

        final float scaleX = getScaleRange() * sectionWidth(bound.width());
        final float scaleY = 1f / (maxY / bound.height());

        if (y.length > 0) {
            for (int i = 0; i < y.length - 1; i++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                points[iX0] = i * scaleX ;
                points[iY0] = -y[i] * scaleY;
                points[iX1] = (i + 1) * scaleX;
                points[iY1] = -y[i + 1] * scaleY;
            }
        }
    }

    public void calculateMatrix(int id, Bound bound, Matrix matrix) {
        final float scaleX = getScaleRange() * sectionWidth(bound.width());
        final float scaleY = 1f / (maxY / bound.height());
        final float dx = (-bound.width() * range.start) * getScaleRange();
        final float offsetX = bound.left + dx + bound.offsetX;
        final float offsetY = bound.bottom + bound.offsetY;
        matrix.setScale(scaleX, scaleY, 0f, 0f);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void calculateMatrixPreview(int id, Bound bound, Matrix matrix) {
        final float width = bound.width();
        final float scaleX = sectionWidth(width);
        final float scaleY = 1f / (maxY / bound.height());
        final float offsetX = bound.left + bound.offsetX;
        final float offsetY = bound.bottom + bound.offsetY;
        matrix.setScale(scaleX, scaleY, 0f, 0f);
        matrix.postTranslate(offsetX, offsetY);
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
        final float scaleY = 1f / (maxY / bound.height());
        final float offsetY = bound.bottom + bound.offsetY;
        point.set(index * scaleX + offsetX, -(getY(id)[index] * scaleY) + offsetY);
    }

    public float sectionWidth(float width) {
        if (x.length > 1) {
            return width / (x.length - 1);
        }
        return width;
    }

    public int size() {
        return lines.length;
    }

    public List<LineRender> initRenders() {
        List<LineRender> lineRenders = new ArrayList<>();
        for (int id = 0; id < lines.length; id++) {
            lineRenders.add(new LineRender(id, this));
        }
        return lineRenders;
    }

    public static final int NONE_INDEX = -1;
}