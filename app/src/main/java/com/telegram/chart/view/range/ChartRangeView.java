package com.telegram.chart.view.range;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;

class ChartRangeView extends BaseRangeView {

    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint touchPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ChartRangeView(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaints(context);
    }

    public ChartRangeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints(context);
    }

    public ChartRangeView(@Nullable Context context) {
        super(context);
        initPaints(context);
    }

    private void initPaints(Context context) {
        backgroundPaint.setColor(Color.CYAN);
        backgroundPaint.setAlpha(70);

        linePaint.setColor(Color.CYAN);
        linePaint.setAlpha(40);

        touchPaint.setColor(Color.CYAN);
        touchPaint.setAlpha(80);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int save = canvas.getSaveCount();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(range);
        } else {
           canvas.clipRect(range, Region.Op.DIFFERENCE);
        }
        canvas.drawRect(bound, backgroundPaint);
        canvas.drawRect(line, linePaint);
        canvas.drawRect(fingerLeft, touchPaint);
        canvas.drawRect(fingerRight, touchPaint);
        canvas.restoreToCount(save);
        canvas.restoreToCount(save);
    }
}