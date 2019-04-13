package com.telegram.chart.view.chart.render;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;

import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public abstract class Render implements Themable {
    protected final Paint[] paint;
    protected final GraphManager manager;
    protected final int[] color;
    protected final Matrix matrix = new Matrix();
    protected int backgroundColor;
    protected final boolean isPreview;

    public Render(GraphManager manager, boolean isPreview) {
        this.manager = manager;
        this.isPreview = isPreview;
        int count = manager.countLines();
        paint = new Paint[count];
        for (int id = 0; id < count; id++) {
            paint[id] = new Paint(Paint.ANTI_ALIAS_FLAG);
        }
        color = new int[this.manager.countLines()];
    }

    public abstract void render(Canvas canvas, RectF chart, RectF visible);

    @Override
    public void applyTheme(Theme theme) {
        backgroundColor = theme.backgroundWindowColor;
        for (int id = 0; id < manager.countLines(); id++){
            if (theme.id == Theme.DAY) {
                color[id] = manager.chart.data[id].color;
            } else {
                color[id] = manager.chart.data[id].colorNight;
            }
            paint[id].setColor(color[id]);
        }
    }

    public void renderSelect(Canvas canvas, int index, RectF chart, RectF visible) {}

    protected int getLower(RectF chart, RectF visible) {
        if (isPreview) {
            return 0;
        }
        final float sectionWidth = manager.sectionWidth(chart.width());
        final int addIndexLower= (int) Math.rint((chart.left - visible.left) / sectionWidth);
        final int lower = manager.chart.getLower(manager.range.start) - 1 - addIndexLower;
        if (lower < 0) {
            return 0;
        }
        return lower;
    }

    protected int getUpper(RectF chart, RectF visible) {
        if (isPreview) {
            return manager.chart.x.length - 1;
        }
        final float sectionWidth = manager.sectionWidth(chart.width());
        final int addIndexUpper = (int) Math.rint((visible.right - chart.right) / sectionWidth);
        final int upper = manager.chart.getUpper(manager.range.end) + 1 + addIndexUpper;
        if (upper > manager.chart.x.length - 1) {
            return manager.chart.x.length - 1;
        }
        return upper;
    }
}
