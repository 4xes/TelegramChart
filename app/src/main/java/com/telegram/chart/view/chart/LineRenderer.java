package com.telegram.chart.view.chart;

import android.graphics.*;
import android.util.Log;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.Arrays;

class LineRenderer implements Renderer {
    private final LineData line;
    private final Path transitionPath = new Path();
    private final Path path = new Path();
    private final Matrix matrix = new Matrix();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private float start = 0.8f;
    private float end = 1f;

    public LineRenderer(LineData lineData) {
        this.line = lineData;
        initPaint();
    }

    private void initPaint() {
        paint.setColor(line.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ViewUtils.pxFromDp(1.5f));
    }

    public void calculatePath(RectF bound, Long maxY, Long minY, float start, float end) {
        if (line.canBeDraw()) {
            float dx = bound.width() / (line.size() - 1);
            float scaleY = (maxY - minY) / bound.height();

            path.reset();
            path.moveTo(bound.left, bound.bottom - ((line.getY(0) - minY) / scaleY));
            for (int i = 0; i < line.size(); i++) {
                path.lineTo(bound.left + i * dx, bound.bottom - ((line.getY(i) - minY) / scaleY));
            }
        }
        changeMatrix(start, end);
    }


    public void changeMatrix(float start, float end) {
        this.start = start;
        this.end = end;
        final float scale = 1f / (end - start);
        matrix.setScale(scale, 1f, 0f, 0f);
        path.transform(matrix, transitionPath);
    }

    @Override
    public void render(RectF bound, Canvas canvas) {
        final int save = canvas.save();
        final float scale = 1f / (end - start);
        final float dx = (-bound.width() * start) * scale;
        canvas.translate(dx, 0f);
        canvas.drawPath(transitionPath, paint);
        canvas.restoreToCount(save);
    }
}