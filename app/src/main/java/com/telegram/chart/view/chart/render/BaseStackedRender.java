package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.utils.ColorUtils;

import static com.telegram.chart.view.chart.GraphManager.NONE_INDEX;

abstract class BaseStackedRender extends Render {
    private final float[][] bars;

    public BaseStackedRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);

        final int linePointsLength = manager.chart.x.length * 4;
        bars = new float[manager.countLines()][linePointsLength];
        initPaints();
    }

    private void initPaints() {
        for (int id = 0; id < manager.countLines(); id++) {
            paint[id].setAntiAlias(false);
            paint[id].setStyle(Paint.Style.STROKE);
            paint[id].setStrokeCap(Paint.Cap.SQUARE);
            paint[id].setStrokeWidth(1);
        }
    }

    public void recalculateBars(RectF bound, int lower, int upper) {
        for (int i = lower; i <= upper; i++) {
            int sum = 0;
            for (int id = 0; id < manager.countLines(); id++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                bars[id][iX0] = i;
                bars[id][iY0] = sum;
                sum -= Math.round(manager.chart.data[id].y[i] * manager.state.chart.percentCurrent[id]) * manager.state.chart.multiCurrent[id];
                bars[id][iX1] = bars[id][iX0];
                bars[id][iY1] = sum;
                if (sum == 0) {
                    bars[id][iY1] = - bound.height();
                    bars[id][iY0] = - bound.height();
                }
            }
        }
    }

    protected abstract void updateMatrix(RectF chart);

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible, int selectIndex) {
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        recalculateBars(chart, lower, upper);
        int saveCount = canvas.save();
        canvas.setMatrix(matrix);
        for (int id = 0; id < manager.countLines(); id++) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                if (isPreview) {
                    canvas.drawLines(bars[id], paint[id]);
                } else {
                    if (selectIndex != NONE_INDEX) {
                        paint[id].setColor(ColorUtils.blendARGB(color[id], colorMask, 0.5f));
                    } else {
                        paint[id].setColor(color[id]);
                    }
                    paint[id].setAlpha(alpha);
                    canvas.drawLines(bars[id], lower * 4, (upper - lower) * 4 + 4, paint[id]);
                    if (selectIndex != NONE_INDEX) {
                        paint[id].setColor(color[id]);
                    }
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }
}