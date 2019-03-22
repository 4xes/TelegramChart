package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

import com.telegram.chart.view.base.Themable;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.chart.Ids;

import static com.telegram.chart.view.utils.ViewUtils.clipSupport;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class RangeView extends BaseRangeView implements Themable<Theme>, Graph.InvalidateListener {

    private Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Graph graph;

    private float selectVerticalWidth = pxFromDp(1f);
    private float selectHorizontalWidth = pxFromDp(4f);
    boolean needInvalidate = true;

    public RangeView(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RangeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RangeView(@Nullable Context context) {
        super(context);
        init();
    }

    private void init() {
    }

    @Override
    public void applyTheme(Theme theme) {
        rangePaint.setColor(theme.getRangeColor());
        selectedPaint.setColor(theme.getRangeSelectedColor());
        invalidate();
    }

    @Override
    public void needInvalidate() {
        invalidate();
    }

    public void seGraph(Graph graph) {
        this.graph = graph;
        this.graph.addListener(this);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        final int saveRange = canvas.save();
        clipSupport(canvas, selectedRange);
        canvas.drawRect(line, rangePaint);
        canvas.restoreToCount(saveRange);
        final int saveSelected = canvas.save();
        clipSupport(canvas, selectedRange.left + selectHorizontalWidth, selectedRange.top + selectVerticalWidth, selectedRange.right - selectHorizontalWidth, selectedRange.bottom - selectVerticalWidth);
        canvas.drawRect(selectedRange, selectedPaint);
        canvas.restoreToCount(saveSelected);
        needInvalidate = false;
    }

    @Override
    public int getViewId() {
        return Ids.RANGE;
    }
}