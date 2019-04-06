package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.chart.Range;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.chart.Ids;

import static com.telegram.chart.view.utils.ViewUtils.clipSupport;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class RangeView extends BaseRangeView implements Themable, Graph.InvalidateListener {

    private Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Graph graph;

    private float selectVerticalWidth = pxFromDp(1f);
    private float selectHorizontalWidth = pxFromDp(4f);
    boolean needInvalidate = true;
    public static final String TAG = RangeView.class.getSimpleName();

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
        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    @Override
    public void applyTheme(Theme theme) {
        rangePaint.setColor(theme.getRangeColor());
        selectedPaint.setColor(theme.getRangeSelectedColor());
        invalidate();
    }

    @Override
    public void needInvalidate() {
        if (BuildConfig.DEBUG) {
            Log.d("RangeView", "needInvalidate");
        }
        invalidate();
    }

    public void seGraph(Graph graph) {
        this.graph = graph;
        this.graph.registerView(getViewId(), this);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDraw");
        }
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