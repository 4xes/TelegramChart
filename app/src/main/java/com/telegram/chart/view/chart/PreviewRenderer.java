package com.telegram.chart.view.chart;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.ViewUtils;

class PreviewRenderer implements Renderer {
    private final LineData line;
    private final Path path = new Path();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PreviewRenderer(LineData lineData) {
        this.line = lineData;
        initPaint();
    }

    private void initPaint() {
        paint.setColor(line.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ViewUtils.pxFromDp(1f));
    }

    public void calculatePath(RectF bound, Long maxY, Long minY) {
        if (line.canBeDraw()) {
            float dx = bound.width() / (line.size() - 1);
            float scaleY = (maxY - minY) / bound.height();
            path.reset();
            path.moveTo(bound.left, bound.bottom - ((line.getY(0) - minY) / scaleY));
            for (int i = 0; i < line.size(); i++) {
                path.lineTo(bound.left + i * dx, bound.bottom - ((line.getY(i) - minY) / scaleY));
            }

        }
    }

    @Override
    public void render(RectF bound, Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}