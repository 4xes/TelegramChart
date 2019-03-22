package com.telegram.chart.view.chart;

import android.graphics.*;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender {
    private final int id;
    private final Graph graph;
    private final Path drawPath = new Path();
    private final Path path = new Path();
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix pathMatrix = new Matrix();
    private final float[] matrix = new float[4];
    private final PointF point = new PointF();
    private final float outerRadius = pxFromDp(7);
    private final float innerRadius = pxFromDp(3);

    public LineRender(int id, Graph data) {
        this.id = id;
        this.graph = data;
        initPaints();
        initPath();
    }

    private void initPaints() {
        paintLine.setColor(graph.getColor(id));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(pxFromDp(1f));
        paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.ROUND);
        paintLine.setPathEffect(new CornerPathEffect(pxFromDp(1f)));

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
        if (graph.state.visible[id]) {
            graph.matrix(id, bound, matrix);
            pathMatrix.reset();
            pathMatrix.setScale(matrix[SCALE_X], matrix[SCALE_Y]);
            pathMatrix.postTranslate(matrix[OFFSET_X], matrix[OFFSET_Y]);
            path.transform(pathMatrix, drawPath);
            canvas.drawPath(drawPath, paintLine);
        }
    }

    public void renderPreview(Canvas canvas, Bound bound) {
        if (graph.state.visible[id]) {
            graph.matrixPreview(id, bound, matrix);
            pathMatrix.reset();
            pathMatrix.setScale(matrix[SCALE_X], matrix[SCALE_Y]);
            pathMatrix.postTranslate(matrix[OFFSET_X], matrix[OFFSET_Y]);
            path.transform(pathMatrix, drawPath);
            canvas.drawPath(drawPath, paintLine);
        }
    }

    public void renderCircle(Canvas canvas, int index, Bound bound) {
        if (graph.state.visible[id]) {
            graph.calculatePoint(id, index, bound, point);
            canvas.drawCircle(point.x, point.y, outerRadius, paintCircle);
            canvas.drawCircle(point.x, point.y, innerRadius, paintInsideCircle);
        }
    }

    private final int SCALE_X = 0;
    private final int SCALE_Y = 1;
    private final int OFFSET_X = 2;
    private final int OFFSET_Y = 3;


    public static List<LineRender> createListRender(Graph graph) {
        List<LineRender> lineRenders = new ArrayList<>();
        for (int id = 0; id < graph.countLines(); id++) {
            LineRender lineRender = new LineRender(id, graph);
            lineRender.setLineWidth(pxFromDp(2f));
            lineRenders.add(lineRender);
        }
        return lineRenders;
    }

    public static List<LineRender> createListRenderPreview(Graph graph) {
        List<LineRender> lineRenders = new ArrayList<>();
        for (int id = 0; id < graph.countLines(); id++) {
            lineRenders.add(new LineRender(id, graph));
        }
        return lineRenders;
    }
}