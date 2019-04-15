package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Theme;

abstract class BasePercentageRender extends Render {
    protected final Path[] path;
    private final PointF point = new PointF();

    public BasePercentageRender(GraphManager manager, boolean isPreview) {
        super(manager, isPreview);
        final int count = manager.countLines();
        path = new Path[count];
        for (int id = 0; id < count; id++) {
            path[id] = new Path();
        }
        initPaints();
    }

    protected void initPaints() {
        for (int id = 0; id < manager.countLines(); id++) {
            if (isPreview) {
                paint[id].setAntiAlias(false);
            } else {
                paint[id].setAntiAlias(true);
            }
            paint[id].setStyle(Paint.Style.FILL);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        super.applyTheme(theme);
        for (int id = 0; id < manager.countLines(); id++) {
            if (theme.id == Theme.DAY) {
                paint[id].setColor(manager.chart.data[id].color);
            } else {
                paint[id].setColor(manager.chart.data[id].colorNight);
            }
        }
    }

    public void recalculateBars(RectF chart, int lower, int upper) {
        final float height = chart.height();
        for (int id = 0; id < manager.countLines(); id++) {
            path[id].reset();
            path[id].moveTo(upper, 0);
            path[id].lineTo(upper, 0);
            path[id].lineTo(lower, 0);
        }
        for (int i = lower; i <= upper; i++) {
            float sum = 0;
            float sumPoint = 0;

            for (int id = 0; id < manager.countLines(); id++) {
                sumPoint += manager.chart.data[id].y[i] * manager.state.chart.percentCurrent[id];
            }

            for (int id = 0; id < manager.countLines(); id++) {
                sum -= Math.ceil((manager.chart.data[id].y[i] * manager.state.chart.percentCurrent[id]) / (sumPoint/ height));
                if (sum < -height) {
                    sum = -height;
                }
                path[id].lineTo(i, sum);
            }
        }

        for (int id = 0; id < manager.countLines(); id++) {
            path[id].close();
            path[id].transform(matrix);
        }
    }

    protected abstract void updateMatrix(RectF chart);

    @Override
    public void render(Canvas canvas, RectF chart, RectF visible, int selectIndex) {
        int lower = getLower(chart, visible);
        int upper = getUpper(chart, visible);
        updateMatrix(chart);
        recalculateBars(chart, lower, upper);
        for (int id = manager.countLines() - 1; id >= 0; id--) {
            float currentAlpha = manager.state.chart.alphaCurrent[id];
            int alpha = (int) Math.ceil(255 * currentAlpha);
            if (alpha != 0) {
                paint[id].setAlpha(alpha);
                canvas.drawPath(path[id], paint[id]);
            }
        }
    }
}