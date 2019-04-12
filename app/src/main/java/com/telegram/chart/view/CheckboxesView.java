package com.telegram.chart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.telegram.chart.view.chart.Checkbox;
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

    public void init(GraphManager manager, OnLineVisibleListener onLineVisibleListener) {
        for (int id = 0; id < manager.countLines(); id++) {
            Checkbox checkBox = new Checkbox(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT
            );
            checkBox.setLayoutParams(params);
            checkBox.setText(manager.chart.data[id].name);
            checkBox.setColor(manager.chart.data[id].color);
            checkBox.setChecked(true);
            checkBox.setTag(id);
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                final int lineId = (int) buttonView.getTag();
                onLineVisibleListener.onLineVisibleChange(lineId, isChecked);
            });
            addView(checkBox);
        }
    }

    @Override
    public void applyTheme(Theme theme) {
        this.theme = theme;
        setBackgroundColor(theme.backgroundWindowColor);

        invalidate();
    }

    public interface OnLineVisibleListener {
        void onLineVisibleChange(int id, boolean isVisible);
    }
}
