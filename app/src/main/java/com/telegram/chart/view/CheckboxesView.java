package com.telegram.chart.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.telegram.chart.R;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;

import static com.telegram.chart.view.utils.ViewUtils.pxFromDp;

public class CheckboxesView extends LinearLayout implements Themable {

    private float dividerPaddingLeft = pxFromDp(56f);
    private final Paint dividerPaint = new Paint();
    private Theme theme;

    public CheckboxesView(Context context) {
        super(context);
        init(context);
    }

    public CheckboxesView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CheckboxesView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setWillNotDraw(false);
        setOrientation(VERTICAL);

        dividerPaint.setStyle(Paint.Style.STROKE);
        dividerPaint.setStrokeWidth(pxFromDp(1f));
    }

    public void init(Graph graph, OnLineVisibleListener onLineVisibleListener) {
        for (int id = 0; id < graph.countLines(); id++) {
            CheckBox checkBox = new CheckBox(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int padding = getResources().getDimensionPixelOffset(R.dimen.normal);
            params.setMargins(padding, 0, 0, 0);
            checkBox.setLayoutParams(params);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setText(graph.getName(id));
            if (theme != null) {
                checkBox.setTextColor(theme.getNameColor());
            }
            checkBox.setChecked(true);
            checkBox.setTag(id);
            checkBox.setOnCheckedChangeListener((buttonView, isVisible) -> {
                final int lineId = (int) buttonView.getTag();
                onLineVisibleListener.onLineVisibleChange(lineId, isVisible);
            });
            CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(graph.getColor(id)));
            addView(checkBox);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        dividerPaint.setColor(theme.getDividerColor());
        setBackgroundColor(theme.getBackgroundWindowColor());

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof CheckBox) {
                ((CheckBox) child).setTextColor(theme.getNameColor());
            }
        }

        invalidate();
    }

    private void drawVerticalSeparators(Canvas canvas) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View child = getChildAt(i);
            final float y = child.getBottom();
            canvas.drawLine(dividerPaddingLeft, y, (float) (canvas.getWidth()), y, dividerPaint);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawVerticalSeparators(canvas);
    }

    public interface OnLineVisibleListener {
        void onLineVisibleChange(int id, boolean isVisible);
    }
}
