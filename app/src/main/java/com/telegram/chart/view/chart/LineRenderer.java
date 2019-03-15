package com.telegram.chart.view.chart;

import android.graphics.*;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.ViewUtils;

class LineRenderer implements Renderer {
    private final LineData line;
    private final Path path = new Path();
    private final Matrix matrix = new Matrix();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineRenderer(LineData lineData) {
        this.line = lineData;
        initPaint();
    }

    private void initPaint() {
        paint.setColor(line.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(ViewUtils.pxFromDp(1f));
    }

    public void calculatePath(RectF bound, Long maxY, Long minY) {
        matrix.setScale(1f, 1f, bound.centerX(), bound.centerY());
        if (line.canBeDraw()) {
            float dx = bound.width() / (line.size() - 1);
            float scaleY = (maxY - minY) / bound.height();

            path.reset();
            path.moveTo(bound.left, bound.bottom - (line.getY(0) * scaleY));
            for (int i = 0; i < line.size(); i++) {
                path.lineTo(bound.left + i * dx, bound.bottom - ((line.getY(i) - minY) / scaleY));
            }
        }
        path.transform(matrix);
    }

    public void changeMatrix(float start, float end) {

    }

    @Override
    public void render(Canvas canvas) {
        canvas.drawPath(path, paint);
    }
}