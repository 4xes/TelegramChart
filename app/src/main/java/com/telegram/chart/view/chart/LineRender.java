package com.telegram.chart.view.chart;

import android.graphics.*;
import android.view.View;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender {
    private final int id;
    private final Graph graph;
    private final Path matrixPath = new Path();
    private final Path path = new Path();
    private final float[] points;
    private final float[] transitionPoints;
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
        initPaints();
        initPath();
        points = new float[graph.maxPoints()];
        transitionPoints = new float[graph.maxPoints()];
        initPoints();
    }

    private void initPaints() {
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

    public void initPoints() {
        long[] y = graph.getY(id);

        if (y.length > 0) {
            for (int i = 0; i < y.length - 1; i++) {
                final int iX0 = i * 4;
                final int iY0 = i * 4 + 1;
                final int iX1 = i * 4 + 2;
                final int iY1 = i * 4 + 3;
                points[iX0] = i;
                points[iY0] = -y[i];
                points[iX1] = (i + 1) ;
                points[iY1] = -y[i + 1];
            }
        }
    }

    public void render(Canvas canvas, Bound bound) {
        if (graph.isVisible(id)) {
            graph.calculateMatrix(id, bound, matrix);
            path.transform(matrix, matrixPath);
            //matrix.mapPoints(transitionPoints, points);
            canvas.drawPath(matrixPath, paintLine);
            //canvas.drawLines(transitionPoints, paintLine);
        }
    }

    public void renderPreview(Canvas canvas, Bound bound) {
        if (graph.isVisible(id)) {
            graph.calculateMatrixPreview(id, bound, matrix);
            //matrix.mapPoints(transitionPoints, points);
            path.transform(matrix, matrixPath);
            canvas.drawPath(matrixPath, paintLine);
            //canvas.drawLines(transitionPoints, paintLine);
        }
    }

    public void renderCircle(Canvas canvas, int index, Bound bound) {
        if (graph.isVisible(id)) {
            graph.calculatePoint(id, index, bound, point);
            canvas.drawCircle(point.x, point.y, outerRadius, paintCircle);
            canvas.drawCircle(point.x, point.y, innerRadius, paintInsideCircle);
        }
    }
}