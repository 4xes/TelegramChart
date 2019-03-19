package com.telegram.chart.view.chart;

import android.graphics.*;

import com.telegram.chart.data.LineData;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRenderer {
    private final LineData line;
    private final Path transitionPath = new Path();
    private final Path path = new Path();
    private final Matrix matrix = new Matrix();
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int windowColor = 0;
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private final float outerRadius = pxFromDp(7);
    private final float innerRadius = pxFromDp(3);

    public LineRenderer(LineData lineData) {
        this.line = lineData;
        initPaint();
        initPath();
    }

    private void initPaint() {
        paint.setColor(line.getColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(pxFromDp(1f));
        paint.setStrokeJoin(Paint.Join.ROUND); // set the join to round you want
        paint.setStrokeCap(Paint.Cap.ROUND); // set the paint cap to round too
        paint.setPathEffect(new CornerPathEffect(pxFromDp(2f))); // set the path effect when they join.

        paintCircle.setStyle(Paint.Style.FILL);
    }

    public void setLineWidth(float lineWidth) {
        paint.setStrokeWidth(lineWidth);
    }

    public void setWindowColor(int color) {
        windowColor = color;
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

    public int getIndex(float touchX, Bound bound, float start, float end) {
        float x = touchX;
        if (x < bound.left) {
            x = bound.left;
        }
        if (x > bound.right) {
            x = bound.right;
        }
        final int count = line.size();
        final int maxIndex = line.size() - 1;
        final float dx = (x - bound.left);
        float percent = (start) + (dx) / bound.width() * (end - start);
        if (count > 1) {
            int index = (int) (percent * maxIndex);
            if (index > maxIndex) {
                return maxIndex;
            }
            return index;
        } else {
            if (line.isNotEmpty()) {
                return 0;
            }
            return -1;
        }
    }

    public void calculatePoint(int index, Bound bound, float start, float end, float maxY, PointF point) {
        final float scaleRange = 1f / (end - start);
        final float scaleX = scaleRange * sectionWidth(bound.width());
        final float dx = (-bound.width() * start) * scaleRange;
        final float offsetX = bound.left + dx + bound.offsetX;
        final float scaleY = 1f / (maxY / bound.height());
        final float offsetY = bound.bottom + bound.offsetY;
        point.set(index * scaleX + offsetX, -(line.getY(index) * scaleY) + offsetY);
    }

    public void render(Canvas canvas, Bound bound, float start, float end, float maxY) {
        changeMatrix(bound, start, end, maxY);
        canvas.drawPath(transitionPath, paint);
    }

    public void renderCircle(Canvas canvas, int selectIndex, Bound bound, float start, float end, float maxY) {
        calculatePoint(selectIndex, bound, start, end, maxY, point);
        paintCircle.setColor(line.getColor());
        canvas.drawCircle(point.x, point.y, outerRadius, paintCircle);
        paintCircle.setColor(windowColor);
        canvas.drawCircle(point.x, point.y, innerRadius, paintCircle);
    }

    private float sectionWidth(Float widthChart) {
        if (line.size() > 1) {
            return widthChart / (line.size() - 1);
        }
        return widthChart;
    }
}