package com.telegram.chart.view.chart;

import android.graphics.*;

import com.telegram.chart.data.LineData;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.core.graphics.ColorUtils;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

class LineRender implements Themable {
    private final int id;
    private final Graph graph;
    private final Paint paintLine = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint paintInsideCircle = new Paint(Paint.ANTI_ALIAS_FLAG);
    public final float[] matrixArray = new float[4];
    public final PointF point = new PointF();
    private final float radius = pxFromDp(4);
    public final float[] points;
    public final float[] drawPoints;
    public final float[] lines;
    public final float[] drawLines;
    public final Matrix matrix = new Matrix();
    public final int lineColor;
    public int backgroundColor;

    public LineRender(int id, Graph data) {
        this.id = id;
        this.graph = data;
        lineColor = graph.getColor(id);
        final int pointsLength = graph.dates.length * 2;
        final int linePointsLength = 4 + (graph.dates.length - 2) * 4;
        lines = new float[linePointsLength];
        drawLines = new float[linePointsLength];
        points = new float[pointsLength];
        drawPoints = new float[pointsLength];
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
                lines[iX0] = i;
                lines[iY0] = -y[i];
                lines[iX1] = (i + 1) ;
                lines[iY1] = -y[i + 1];

                points[i * 2] = i;
                points[(i * 2) + 1] = -y[i];
            }
            final int lastI = (y.length - 1) * 2;
            points[lastI] = y.length - 1;
            points[lastI + 1] = -y[y.length - 1];
        }
    }

    private void initPaints() {
        final float stroke = pxFromDp(1f);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setColor(lineColor);
        paintLine.setStrokeWidth(stroke);
        paintLine.setStrokeCap(Paint.Cap.BUTT);
        paintPoint.setStyle(Paint.Style.STROKE);
        paintPoint.setColor(lineColor);
        paintPoint.setStrokeCap(Paint.Cap.ROUND);
        paintPoint.setStrokeWidth(stroke);
        paintCircle.setStyle(Paint.Style.STROKE);
        paintCircle.setColor(lineColor);
        paintInsideCircle.setStyle(Paint.Style.FILL);
    }

    public void setLineWidth(float strokeWidth) {
        paintLine.setStrokeWidth(strokeWidth);
        paintPoint.setStrokeWidth(strokeWidth);
        paintCircle.setStrokeWidth(strokeWidth);
    }

    @Override
    public void applyTheme(Theme theme) {
        backgroundColor = theme.getBackgroundWindowColor();
        paintInsideCircle.setColor(backgroundColor);
    }

    public void recalculateLines(RectF r, int lower, int upper) {
        graph.matrix(id, r, matrixArray);
        matrix.reset();
        matrix.setScale(matrixArray[SCALE_X], matrixArray[SCALE_Y]);
        matrix.postTranslate(matrixArray[OFFSET_X], matrixArray[OFFSET_Y]);
        matrix.mapPoints(drawLines, lower * 4, lines, lower * 4, (upper - lower) * 2);
        matrix.mapPoints(drawPoints, lower * 2, points, lower * 2, (upper - lower) + 1);
    }

    public void calculatePreviewPoints(RectF r) {
        graph.matrixPreview(id, r, matrixArray);
        matrix.reset();
        matrix.setScale(matrixArray[SCALE_X], matrixArray[SCALE_Y]);
        matrix.postTranslate(matrixArray[OFFSET_X], matrixArray[OFFSET_Y]);
        matrix.mapPoints(drawLines, lines);
    }

    public void render(Canvas canvas, RectF r) {
        final int maxIndex = graph.getY(id).length - 1;
        int lowerId = LineData.getLowerIndex(graph.range.start, maxIndex) - 1;
        if (lowerId < 0) {
            lowerId = 0;
        }
        int upperId = LineData.getUpperIndex(graph.range.end, maxIndex) + 1;
        if (upperId > maxIndex){
            upperId = maxIndex;
        }
        float currentAlpha = graph.state.chart.alphaCurrent[id];
        if (currentAlpha != 0f) {
            recalculateLines(r, lowerId, upperId);
            final int blendColor = ViewUtils.blendARGB( backgroundColor, lineColor, currentAlpha);
            paintLine.setColor(blendColor);
            paintPoint.setColor(blendColor);
            canvas.drawLines(drawLines, lowerId * 4, (upperId - lowerId) * 4, paintLine);
            canvas.drawPoints(drawPoints, lowerId * 2, (upperId - lowerId) * 2 + 2, paintPoint);
        }
    }

    public void renderPreview(Canvas canvas, RectF r) {
        float currentAlpha = graph.state.chart.alphaCurrent[id];
        if (currentAlpha != 0f) {
            calculatePreviewPoints(r);
            final int blendColor = ViewUtils.blendARGB( backgroundColor, lineColor, currentAlpha);
            paintLine.setColor(blendColor);
            canvas.drawLines(drawLines, paintLine);
        }
    }

    public void renderCircle(Canvas canvas, int index, RectF r) {
        float currentAlpha = graph.state.chart.alphaCurrent[id];
        if (currentAlpha != 0f) {
            graph.calculatePoint(id, index, r, point);
            final int blendColor = ViewUtils.blendARGB( backgroundColor, lineColor, currentAlpha);
            paintCircle.setColor(blendColor);
            canvas.drawCircle(point.x, point.y, radius, paintInsideCircle);
            canvas.drawCircle(point.x, point.y, radius, paintCircle);
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