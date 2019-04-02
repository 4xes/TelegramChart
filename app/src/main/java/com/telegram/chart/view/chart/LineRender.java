package com.telegram.chart.view.chart;

import android.graphics.*;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import java.util.ArrayList;
import java.util.List;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender implements Themable {
    private final int id;
    private final Graph graph;
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] matrix = new float[4];
    private final PointF point = new PointF();
    private final float outerRadius = pxFromDp(7);
    private final float innerRadius = pxFromDp(3);
    public final float[] points;
    public final float[] transitionPoints;

    public LineRender(int id, Graph data) {
        this.id = id;
        this.graph = data;
        points = new float[graph.maxPoints()];
        transitionPoints = new float[graph.maxPoints()];
        initPaints();
        initPoints();
    }

    public void initPoints() {
        int[] y = graph.getY(id);

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

    private void initPaints() {
        paintLine.setColor(graph.getColor(id));
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(pxFromDp(1f));
        //paintLine.setStrokeJoin(Paint.Join.ROUND);
        paintLine.setStrokeCap(Paint.Cap.SQUARE);
//        paintLine.setPathEffect(new CornerPathEffect(pxFromDp(1f)));

        paintCircle.setStyle(Paint.Style.FILL);
        paintCircle.setColor(graph.getColor(id));
        paintInsideCircle.setStyle(Paint.Style.FILL);
    }

    public void setLineWidth(float strokeWidth) {
        paintLine.setStrokeWidth(strokeWidth);
    }

    @Override
    public void applyTheme(Theme theme) {
        paintInsideCircle.setColor(theme.getBackgroundWindowColor());
    }

    public void calculatePoints(RectF r, int lower, int upper) {
        graph.matrix(id, r, matrix);
        for (int i = lower; i < upper; i = i + 2) {
            final int iY0 = i + 1;
            transitionPoints[i] = points[i] * matrix[SCALE_X] + matrix[OFFSET_X];
            transitionPoints[iY0] = points[iY0] * matrix[SCALE_Y] + matrix[OFFSET_Y];
        }
    }

    public void calculatePreviewPoints(RectF r) {
        graph.matrixPreview(id, r, matrix);
        for (int i = 0; i < (graph.getY(id).length - 1) * 4; i = i + 2) {
            final int iY0 = i + 1;
            transitionPoints[i] = (float) Math.ceil(points[i] * matrix[SCALE_X] + matrix[OFFSET_X]);
            transitionPoints[iY0] = (float) Math.ceil(points[iY0] * matrix[SCALE_Y] + matrix[OFFSET_Y]);
        }
    }

    public void render(Canvas canvas, RectF r) {
        int lowerId = LineData.getLowerIndex(graph.range.start, graph.getY(id).length - 1) * 4;
        int upperId = LineData.getUpperIndex(graph.range.end, graph.getY(id).length - 1) * 4;
        float currentAlpha = graph.state.chart.alphaCurrent[id];
        if (currentAlpha != 0f) {
            calculatePoints(r, lowerId, upperId);
            int newAlpha = (int)(currentAlpha * 255);
            paintLine.setAlpha(newAlpha);
            if (upperId - lowerId < 80) {
                paintLine.setStrokeCap(Paint.Cap.ROUND);
            } else {
                paintLine.setStrokeCap(Paint.Cap.SQUARE);
            }
            canvas.drawLines(transitionPoints, lowerId, upperId - lowerId, paintLine);
        }
    }

    public void renderPreview(Canvas canvas, RectF r) {
        float currentAlpha = graph.state.chart.alphaCurrent[id];
        if (currentAlpha != 0f) {
            calculatePreviewPoints(r);
            int newAlpha = (int)(currentAlpha * 255);
            paintLine.setAlpha(newAlpha);
            canvas.drawLines(transitionPoints, paintLine);
        }
    }

    public void renderCircle(Canvas canvas, int index, RectF r) {
        if (graph.state.visible[id]) {
            graph.calculatePoint(id, index, r, point);
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
            lineRender.setLineWidth(pxFromDp(1.5f));
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