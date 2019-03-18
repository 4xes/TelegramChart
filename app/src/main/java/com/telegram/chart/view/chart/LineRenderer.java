package com.telegram.chart.view.chart;

import android.graphics.*;
import android.util.Log;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.Arrays;

class LineRenderer {
    private final LineData line;
    private final Path transitionPath = new Path();
    private final Path path = new Path();
    private final Matrix matrix = new Matrix();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineRenderer(LineData lineData, float lineWidth) {
        this.line = lineData;
        initPaint(lineWidth);
        initPath();
    }

    private void initPaint(float lineWidth) {
        paint.setColor(line.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setStrokeJoin(Paint.Join.ROUND);    // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND);  // set the paint cap to round too
        CornerPathEffect cornerPathEffect = new CornerPathEffect(ViewUtils.pxFromDp(2f)); // set the path effect when they join.
        paint.setPathEffect(cornerPathEffect);
    }

    private void initPath() {
        if (line.isNotEmpty()) {
            path.moveTo(0, - (line.getY(0)));
            for (int i = 0; i < line.size(); i++) {
                path.lineTo(i, - (line.getY(i)));
            }
        }
    }

    private void changeMatrix(Bound bound, float start, float end, float maxY) {
        matrix.reset();
        final float scaleRange = 1f / (end - start);
        final float scaleX = scaleRange * sectionWidth(bound.width());
        final float scaleY = 1f / (maxY / bound.height());
        final float dx = (-bound.width() * start) * scaleRange;
        final float offsetX = bound.left + dx + bound.offsetX;
        final float offsetY = bound.bottom + bound.offsetY;
        matrix.setScale(scaleX, scaleY, 0f, 0f);
        matrix.postTranslate(offsetX, offsetY);
        path.transform(matrix, transitionPath);
    }

    private int getIndex(float x, Bound bound, float start, float end) {
//        final float scaleRange = 1f / (end - start);
//        final float scaleX = scaleRange * sectionWidth(bound.width());
//        return start + ((x - bound.offsetX) / scaleX);
        return 0;
    }

    public void render(Canvas canvas, Bound bound, float start, float end, float maxY) {
        changeMatrix(bound, start, end, maxY);
        canvas.drawPath(transitionPath, paint);
    }

    private float sectionWidth(Float widthChart) {
        if (line.size() > 1) {
            return widthChart / (line.size() - 1);
        }
        return widthChart;
    }
}