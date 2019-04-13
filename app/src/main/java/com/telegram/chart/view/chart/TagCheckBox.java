package com.telegram.chart.view.chart;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
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
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.ColorUtils;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;
import static com.telegram.chart.view.utils.ViewUtils.pxFromSp;
import static com.telegram.chart.view.utils.ViewUtils.reconcileSize;

public class TagCheckBox extends View implements Checkable, ValueAnimator.AnimatorUpdateListener, View.OnClickListener, Themable {
    protected RectF bound = new RectF();
    private String text = "";
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Theme theme;

    private OnCheckedChangeListener onCheckedChangeListener;

    private ValueAnimator animator = null;
    private int color;
    private int colorNight;
    private int backgroundColor;
    private float progress = 1f;
    private boolean isChecked = true;

    private float lineWidth = pxFromDp(2f);
    private float strokeWidth = pxFromDp(2f);
    private final float RADIUS = pxFromDp(18f);
    private final float HEIGHT = pxFromDp(36f);
    private final float textLeftPadding = pxFromDp(16f);
    private final float markOffsetX = -pxFromDp(4f);
    private final float textRightPadding = pxFromDp(16f);
    private final float markWidth = pxFromDp(10f);
    private final float markHeight = pxFromDp(8f);
    private final float markMiddleSize = pxFromDp(3f);
    private final float markPadding = pxFromDp(8f);
    private Matrix matrix = new Matrix();
    private float[] mark = new float[8];
    private float[] markDraw = new float[8];

    public TagCheckBox(@NonNull Context context, @Nullable AttributeSet attrs, @Nullable int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public TagCheckBox(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TagCheckBox(@Nullable Context context) {
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
        textPaint.setTextAlign(Paint.Align.CENTER);
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

    public void setColor(int color, int colorNight) {
        this.color = color;
        this.colorNight = colorNight;
        if (theme != null) {
            applyTheme(theme);
        }
        invalidate();
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        this.backgroundColor = theme.backgroundWindowColor;
        if (theme.id == Theme.DAY) {
            strokePaint.setColor(this.color);
            fillPaint.setColor(this.color);
        } else {
            strokePaint.setColor(this.colorNight);
            fillPaint.setColor(this.colorNight);
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widthText = textPaint.measureText(text);
        final int minWidth = Math.round(textLeftPadding + markWidth + markPadding + markOffsetX + widthText + textRightPadding);
        final int minHeight = Math.round(HEIGHT);

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

        float diff = ((bound.height() - markHeight) / 2f);
        float topY = bound.top + diff;
        float bottomY = topY + markHeight;
        float leftY = bottomY - markMiddleSize;
        float leftX = bound.left + textLeftPadding + markOffsetX;
        float topX = leftX + markWidth;
        float bottomX = leftX + markMiddleSize;
        mark[0] = leftX;
        mark[1] = leftY;
        mark[2] = bottomX;
        mark[3] = bottomY;
        mark[4] = mark[2];
        mark[5] = mark[3];
        mark[6] = topX;
        mark[7] = topY;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int currentAlpha = (int) Math.ceil(255 * progress);
        if (currentAlpha != 0) {
            fillPaint.setAlpha(currentAlpha);
            canvas.drawRoundRect(bound, RADIUS, RADIUS, fillPaint);
            linePaint.setAlpha(currentAlpha);
            matrix.reset();
            matrix.setScale(progress, progress, mark[2], mark[3]);
            matrix.mapPoints(markDraw, mark);

            linePaint.setColor(ColorUtils.blendARGB(backgroundColor, Color.WHITE, progress));
            canvas.drawLines(markDraw, linePaint);
        }
        canvas.drawRoundRect(bound, RADIUS, RADIUS, strokePaint);

        textPaint.setColor(ColorUtils.blendARGB(color, Color.WHITE, progress));
        float textHeight = textPaint.descent() - textPaint.ascent();
        float textOffset = (textHeight / 2) - textPaint.descent();
        float offsetXText = (markWidth + markPadding) * progress * 0.5f;
        canvas.drawText(text, bound.centerX() + offsetXText, bound.centerY() + textOffset, textPaint);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
        this.onCheckedChangeListener = onCheckedChangeListener;
    }

    private void toggleAnimate(boolean isChecked) {
        ValueAnimator animator = this.animator;
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofFloat(progress, isChecked? 1f: 0f);
        animator.addUpdateListener(this);
        animator.start();
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
        progress = (float) animation.getAnimatedValue();
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
        void onCheckedChanged(TagCheckBox buttonView, boolean isChecked);
    }
}