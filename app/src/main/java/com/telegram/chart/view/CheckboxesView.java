package com.telegram.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.telegram.chart.R;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.flow.FlowLayout;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;

public class CheckboxesView extends FlowLayout implements Themable {
    private Theme theme;

    public CheckboxesView(Context context) {
        super(context);
    }

    public CheckboxesView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(GraphManager graphManager, OnLineVisibleListener onLineVisibleListener) {
        for (int id = 0; id < graphManager.countLines(); id++) {
            CheckBox checkBox = new CheckBox(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            int padding = getResources().getDimensionPixelOffset(R.dimen.normal);
            params.setMargins(padding, 0, 0, 0);
            checkBox.setLayoutParams(params);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setText(graphManager.chart.data[id].name);
            if (theme != null) {
                checkBox.setTextColor(theme.nameColor);
            }
            checkBox.setChecked(true);
            checkBox.setTag(id);
            checkBox.setOnCheckedChangeListener((buttonView, isVisible) -> {
                final int lineId = (int) buttonView.getTag();
                onLineVisibleListener.onLineVisibleChange(lineId, isVisible);
            });
            addView(checkBox);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setBackgroundColor(theme.backgroundWindowColor);

        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child instanceof CheckBox) {
                ((CheckBox) child).setTextColor(theme.nameColor);
            }
        }

        invalidate();
    }

    public interface OnLineVisibleListener {
        void onLineVisibleChange(int id, boolean isVisible);
    }
}
