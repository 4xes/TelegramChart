package com.telegram.chart.view.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;

import com.telegram.chart.view.annotation.NonNull;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.utils.ViewUtils;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;
import static com.telegram.chart.view.utils.ViewUtils.reconcileSize;

public class Checkbox extends View implements Checkable, ValueAnimator.AnimatorUpdateListener, View.OnClickListener {
    protected RectF bound = new RectF();
    private String text = "";
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnCheckedChangeListener onCheckedChangeListener;

    private ValueAnimator animator = null;
    private int color;
    private int currentAlpha = 255;
    private boolean isChecked = true;

    private float lineWidth = pxFromDp(2f);
    private float strokeWidth = pxFromDp(2f);
    private final float RADIUS = pxFromDp(18f);
    private final float textLeftPadding = pxFromDp(28f);
    private final float textRightPadding = pxFromDp(16f);

    public Checkbox(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Checkbox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Checkbox(@Nullable Context context) {
        super(context);
        init();
    }

    private void init() {
        initPaint();

        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        toggle();
    }

    private void initPaint() {
        linePaint.setStrokeWidth(lineWidth);
        linePaint.setColor(Color.WHITE);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeCap(Paint.Cap.ROUND);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(strokeWidth);
        fillPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(pxFromSp(14f));
        textPaint.setTextAlign(Paint.Align.LEFT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textPaint.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));
        } else {
            textPaint.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
        }
    }

    public void setText(String text) {
        if (text == null) {
            return;
        }
        this.text = text;
        requestLayout();
    }

    public void setColor(int color) {
        this.color = color;
        strokePaint.setColor(color);
        fillPaint.setColor(color);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minWidth = Math.round(textLeftPadding + textPaint.measureText(text) + textRightPadding);
        final int minHeight = pxFromDp(38);

        int measuredWidth = reconcileSize(minWidth, widthMeasureSpec);
        int measuredHeight = reconcileSize(minHeight, heightMeasureSpec);

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        bound.left = getPaddingLeft();
        bound.top = getPaddingTop();
        bound.right = getWidth() - getPaddingRight();
        bound.bottom = getHeight() - getPaddingBottom();
        bound.inset(strokeWidth, strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (currentAlpha != 0) {
            fillPaint.setAlpha(currentAlpha);
            canvas.drawRoundRect(bound, RADIUS, RADIUS, fillPaint);
        }
        canvas.drawRoundRect(bound, RADIUS, RADIUS, strokePaint);

        textPaint.setColor(ViewUtils.blendARGB(color, Color.WHITE, (float) currentAlpha / 255));
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();
        canvas.drawText(text, bound.left + textLeftPadding, bound.centerY() + textOffset, textPaint);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private void toggleAnimate(boolean isChecked) {
        ValueAnimator animator = this.animator;
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofInt(currentAlpha, isChecked? 255: 0);
        animator.addUpdateListener(this);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        currentAlpha = (int) animation.getAnimatedValue();
        invalidate();
    }

    @Override
    public void setChecked(boolean checked) {
        if (isChecked != checked) {
            isChecked = checked;
            toggleAnimate(isChecked);
        }
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        isChecked = !isChecked;
        onCheckedChangeListener.onCheckedChanged(this, isChecked);
        toggleAnimate(isChecked);
    }

    public interface OnCheckedChangeListener {
        void onCheckedChanged(Checkbox buttonView, boolean isChecked);
    }
}