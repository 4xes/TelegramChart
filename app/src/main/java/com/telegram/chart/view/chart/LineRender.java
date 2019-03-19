package com.telegram.chart.view.chart;

import android.graphics.*;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender {
    private final int id;
    private final Graph graph;
    private final Path matrixPath = new Path();
    private final Path path = new Path();
    private final Matrix matrix = new Matrix();
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final PointF point = new PointF();
    private final float outerRadius = pxFromDp(7);
    private final float innerRadius = pxFromDp(3);

    public LineRender(int id, Graph data) {
        this.id = id;
        this.graph = data;
        initPaint();
        initPath();
    }

    private void initPaint() {
        paintLine.setColor(graph.getColor(id));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(pxFromDp(1f));
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setPathEffect(new CornerPathEffect(pxFromDp(2f)));

        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(graph.getColor(id));
        paintInsideCircle.setStyle(Paint.Style.FILL);
    }

    public void setLineWidth(float strokeWidth) {
        paintLine.setStrokeWidth(strokeWidth);
    }

    public void setWindowColor(int windowColor) {
        paintInsideCircle.setColor(windowColor);
    }

    private void initPath() {
        long[] y = graph.getY(id);

        if (y.length > 0) {
            path.moveTo(0, -y[0]);
            for (int i = 0; i < y.length; i++) {
                path.lineTo(i, -y[i]);
            }
        }
    }

    public void render(Canvas canvas, Bound bound) {
        graph.calculateMatrix(id, bound, path, matrixPath, matrix);
        canvas.drawPath(matrixPath, paintLine);
    }

    public void renderPreview(Canvas canvas, Bound bound) {
        graph.calculateMatrixPreview(id, bound, path, matrixPath, matrix);
        canvas.drawPath(matrixPath, paintLine);
    }

    public void renderCircle(Canvas canvas, int index, Bound bound) {
        graph.calculatePoint(id, index, bound, point);
        canvas.drawCircle(point.x, point.y, outerRadius, paintCircle);
        canvas.drawCircle(point.x, point.y, innerRadius, paintInsideCircle);
    }
}