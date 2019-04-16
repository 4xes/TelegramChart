package com.telegram.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

import com.telegram.chart.data.Chart;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.chart.TagCheckBox;
import com.telegram.chart.view.flow.FlowLayout;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public class CheckboxesView extends FlowLayout implements
        GraphManager.OnShowCheckboxes, Themable {
    private Theme theme;

    public CheckboxesView(Context context) {
        super(context);
    }

    public CheckboxesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(GraphManager manager, OnLineVisibleListener onLineVisibleListener, boolean renderHide) {
        manager.setOnShowCheckboxes(this);
        int count = renderHide ? 3: manager.countLines();
        for (int id = 0; id < count; id++) {
            TagCheckBox checkBox = new TagCheckBox(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            checkBox.setLayoutParams(params);
            if (!renderHide) {
                checkBox.setText(manager.chart.data[id].name);
                checkBox.setColor(manager.chart.data[id].color, manager.chart.data[id].colorNight);
            }
            checkBox.setChecked(true);
            checkBox.setTag(id);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final int lineId = (int) buttonView.getTag();
                onLineVisibleListener.onLineVisibleChange(lineId, isChecked);
            });
            if (renderHide) {
                checkBox.setVisibility(View.GONE);
            }
            addView(checkBox);

        }
    }

    @Override
    public void onShow(Chart chart) {
        for (int id = 0; id < getChildCount(); id++) {
            View child = getChildAt(id);
            if (child instanceof TagCheckBox) {
                TagCheckBox checkBox = (TagCheckBox) child;
                checkBox.setText(chart.data[id].name);
                checkBox.setColor(chart.data[id].color, chart.data[id].colorNight);
                checkBox.setVisibility(VISIBLE);
                ScaleAnimation fade_in = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                fade_in.setDuration(300);
                fade_in.setFillAfter(true);
                checkBox.startAnimation(fade_in);
            }
        }
    }

    @Override
    public void onRemove() {
        for (int id = 0; id < getChildCount(); id++) {
            View child = getChildAt(id);
            if (child instanceof TagCheckBox) {
                TagCheckBox checkBox = (TagCheckBox) child;
                ScaleAnimation fade_in = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                fade_in.setDuration(300);
                fade_in.setFillAfter(true);
                checkBox.startAnimation(fade_in);
                fade_in.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        checkBox.setVisibility(GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
            }
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setBackgroundColor(theme.contentColor);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof TagCheckBox) {
                ((TagCheckBox) child).applyTheme(theme);
            }
        }
        invalidate();
    }

    public interface OnLineVisibleListener {
        void onLineVisibleChange(int id, boolean isVisible);
    }
}
