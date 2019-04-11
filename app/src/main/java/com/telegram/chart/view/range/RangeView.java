package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.telegram.chart.BuildConfig;
import com.telegram.chart.view.annotation.NonNull;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.chart.Ids;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

import static com.telegram.chart.view.utils.ViewUtils.clipSupport;
import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class RangeView extends BaseRangeView implements Themable, GraphManager.InvalidateListener {

    private Paint selectedPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint rangePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private GraphManager manager;

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
        rangePaint.setColor(theme.rangeColor);
        selectedPaint.setColor(theme.rangeSelectedColor);
        invalidate();
    }

    @Override
    public void needInvalidate() {
        if (BuildConfig.DEBUG) {
            Log.d("RangeView", "needInvalidate");
        }
        invalidate();
    }

    public void seGraph(GraphManager graphManager) {
        this.manager = graphManager;
        this.manager.registerView(getViewId(), this);
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