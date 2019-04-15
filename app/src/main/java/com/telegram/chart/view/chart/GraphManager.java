package com.telegram.chart.view.chart;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;

import com.telegram.chart.data.Chart;
import com.telegram.chart.data.parser.ChartsInteractor;
import com.telegram.chart.data.parser.DataInteractorImpl;
import com.telegram.chart.view.chart.state.StateFabric;
import com.telegram.chart.view.chart.state.StateManager;
import com.telegram.chart.view.utils.DateUtils;

public class GraphManager {
    public final StateManager state;
    public GraphManager zoomManager;
    private ChartsInteractor interactor = null;
    private int num;

    public final Chart chart;
    public Range range;
    public boolean isZoom = false;

    public void setVisible(int id, boolean isVisible) {
        if (zoomManager != null) {
            zoomManager.setVisible(id, isVisible);
            if (chart.isBar) {
                return;
            }
        }
        if (chart.visible[id] == isVisible) {
            return;
        }
        chart.visible[id] = isVisible;
        state.setAnimationHide(id);

        invalidateById(Ids.CHART);
        invalidateById(Ids.PREVIEW);
    }

    public void update(int id, float start, float end) {
        if (zoomManager != null) {
            zoomManager.update(id, start, end);
        }
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
            if (chart.visible[id]) {
                count++;
            }
        }
        return count;
    }

    private final InvalidateListener[] invalidateListeners = new InvalidateListener[3];

    public GraphManager(int num, Context context, Chart chart) {
        if (context != null) {
            this.interactor = new DataInteractorImpl(context);
        }
        this.num = num;
        this.chart = chart;
        this.range = new Range();
        this.state = StateFabric.getStateManager(this);
    }

    public GraphManager(Chart chart, Range range) {
        this.chart = chart;
        this.range = range;
        this.state = StateFabric.getStateManager(this);
        this.isZoom = true;
    }

    public void registerView(int id, InvalidateListener invalidateListener) {
        invalidateListeners[id] = invalidateListener;
    }

    public interface InvalidateListener {
        void needInvalidate();
        int getViewId();
    }

    public int getIndex(float x, RectF r) {
        int lower = chart.getLower(range.start);
        int upper =  chart.getUpper(range.end);
        int index = (int) (Math.ceil(x - r.left - (-r.width() * range.start) * getScaleRange()) / (getScaleRange() * sectionWidth(r.width())));
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
        final float scaleY = (1f / ((state.chart.yMaxCurrent[id] - state.chart.yMinCurrent[id]) / r.height())) * state.chart.multiCurrent[id];
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom + (scaleY * state.chart.yMinCurrent[id] * state.chart.multiCurrent[id]);
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixBar(RectF r, Matrix matrix) {
        final float width = r.width();
        final float scaleX = getScaleRange() * barWidth(width);
        final float scaleY = (1f / (state.chart.yMaxCurrent[0] / r.height())) * state.chart.multiCurrent[0];
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixBarPreview(RectF r, Matrix matrix) {
        final float width = r.width();
        final float scaleX = barWidth(width);
        final float scaleY = (1f / (state.preview.yMaxCurrent[0] / r.height()) * state.preview.multiCurrent[0]);
        final float offsetX = r.left + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixStacked(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = getScaleRange() * barWidth(width);
        final float scaleY = (1f / (state.chart.maxCurrent / r.height()));
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPreviewStacked(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = barWidth(width);
        final float scaleY = (1f / (state.preview.maxCurrent / r.height()));
        final float offsetX = r.left + scaleX / 2f;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPercentage(RectF r,  Matrix matrix) {
        final float width = r.width();
        final float scaleX = getScaleRange() * sectionWidth(width);
        final float scaleY = 1f;
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom;
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void matrixPercentagePreview(RectF r, Matrix matrix) {
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
        final float offsetY = r.bottom + (scaleY * state.preview.yMinCurrent[id] * state.preview.multiCurrent[id]);
        matrix.reset();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(offsetX, offsetY);
    }

    public void calculateLine(int index, RectF r, PointF point) {
        calculatePoint(0, index, r, point);
        float maxY = point.y;
        for (int id = 0; id < countLines(); id++) {
            if (chart.visible[id]) {
                calculatePoint(id, index, r, point);
                if (maxY > point.y) {
                    maxY = point.y;
                }
            }
        }
        point.y = maxY;
    }

    public void calculatePoint(int id, int index, RectF r, PointF point) {
        final float width = r.width();
        final float scaleX = getScaleRange() * sectionWidth(width);
        final float scaleY = (1f / ((state.chart.yMaxCurrent[id] - state.chart.yMinCurrent[id]) / r.height())) * state.chart.multiCurrent[id];
        final float dx = (-width * range.start) * getScaleRange();
        final float offsetX = r.left + dx;
        final float offsetY = r.bottom + (scaleY * state.chart.yMinCurrent[id]);
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
        if (zoomManager != null) {
            zoomManager.onTimeUpdate(deltaTime);
        }
        state.tick();
        if (state.chart.needInvalidate) {
            invalidateById(Ids.CHART);
        }
        if (state.preview.needInvalidate) {
            invalidateById(Ids.PREVIEW);
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());

    public void onZoom(int index) {
        if (!(chart.isLined || chart.isStacked || chart.isBar)) {
            return;
        }
        if (interactor != null) {
            try {
                Chart zoomChart = interactor.getChart(DateUtils.getPath(num, chart.x[index] * 1000L));
                for (int id = 0; id < countLines(); id++) {
                    zoomChart.visible[id] = chart.visible[id];
                }
                handler.post(() -> {
                    ((ChartView) invalidateListeners[Ids.CHART]).resetIndex();
                    zoomManager = new GraphManager(zoomChart, range);
                    zoomManager.registerView(Ids.CHART, invalidateListeners[Ids.CHART]);
                    zoomManager.registerView(Ids.PREVIEW, invalidateListeners[Ids.PREVIEW]);
                    zoomManager.registerView(Ids.RANGE, invalidateListeners[Ids.RANGE]);
                    state.resetZoom(true);
                });
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }
    }

    public static final int NONE_INDEX = -1;
}