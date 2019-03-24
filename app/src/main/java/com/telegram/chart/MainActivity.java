package com.telegram.chart;

import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.widget.CompoundButtonCompat;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.ChartsInteractor;
import com.telegram.chart.data.DataInteractorImpl;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.range.RangeView;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.chart.ChartView;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.chart.InfoView;
import com.telegram.chart.view.range.BaseRangeView;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class MainActivity extends ThemeBaseActivity {

    private LinearLayout ll;
    private List<ChartView> chartViews = new ArrayList<>();
    private List<Themable> themables = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_title);
        initViews();
        applyTheme(getCurrentTheme());
        loadChart();
    }

    private void initViews() {
        ll = findViewById(R.id.ll);
    }

    private void renderCheckboxes(final Graph graph, final InfoView infoView) {
        for (int id = 0; id < graph.countLines(); id++) {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int padding = getResources().getDimensionPixelOffset(R.dimen.normal);
            params.setMargins(padding, 0, 0, 0);
            checkBox.setLayoutParams(params);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setText(graph.getName(id));
            checkBox.setTextColor(getCurrentTheme().getNameColor());
            checkBox.setChecked(true);
            checkBox.setTag(id);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    graph.setVisible((int) buttonView.getTag(), isChecked);
                    if (infoView.isShowing()) {
                       infoView.invalidate();
                    }
                }
            });
            CompoundButtonCompat.setButtonTintList(checkBox, ColorStateList.valueOf(graph.getColor(id)));
            ll.addView(checkBox);
            if (id != graph.countLines() - 1) {
                renderDivider(true, ViewUtils.pxFromDp(1), DIVIDER_TAG);
            }
        }
    }

    private void renderDivider(boolean withMargin, int height, String tag) {
        View line = new View(this);
        LinearLayout.LayoutParams dividerParams = new LinearLayout.LayoutParams(
                MATCH_PARENT,
                height
        );
        if (withMargin) {
            dividerParams.setMargins(ViewUtils.pxFromDp(64), 0, 0, 0);
        }
        line.setTag(tag);
        line.setLayoutParams(dividerParams);
        ll.addView(line);
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (ChartView chartView: chartViews) {
            if (chartView != null) {
                chartView.onSubscribe();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        for (ChartView chartView: chartViews) {
            if (chartView != null) {
                chartView.onDescribe();
            }
        }
    }

    private void renderChart(int number, final ChartData chart) {
        final Graph graph = new Graph(chart);
        LayoutInflater li = LayoutInflater.from(this);
        final View chartWrapper = li.inflate(R.layout.chart_layout, ll, false);
        final View previewWrapper = li.inflate(R.layout.preview_layout, ll, false);
        final InfoView infoView = chartWrapper.findViewById(R.id.info);
        final ChartView chartView = chartWrapper.findViewById(R.id.chart);
        final RangeView rangeView = previewWrapper.findViewById(R.id.range);
        final PreviewChartView previewChartView = previewWrapper.findViewById(R.id.preview);
        final TextView titleView = chartWrapper.findViewById(R.id.title);
        titleView.setText(getString(R.string.chart_title, number));

        themables.add(infoView);
        themables.add(chartView);
        themables.add(rangeView);
        ll.addView(chartWrapper);
        ll.addView(previewWrapper);
        renderCheckboxes(graph, infoView);
        renderDivider(false, ViewUtils.pxFromDp(1), DIVIDER_TAG);
        renderDivider(false, ViewUtils.pxFromDp(40), SPACING_TAG);

        chartView.seGraph(graph);
        previewChartView.setGraph(graph);
        rangeView.seGraph(graph);
        infoView.seGraph(graph);
        rangeView.setOnRangeListener(new BaseRangeView.OnRangeListener() {
            @Override
            public void onChangeRange(Float start, Float end) {
                chartView.resetIndex();
                infoView.hide();
                graph.update(rangeView.getViewId(), start, end);
            }
        });
        chartView.setOnShowInfoListener(new ChartView.OnShowInfoListener() {
            @Override
            public void showInfo(int index, RectF bound, PointF point) {
                infoView.showInfo(index, bound, point);
            }
        });
    }

    private void loadChart() {
        ChartsInteractor interactor = new DataInteractorImpl(getApplicationContext());
        try {
            final List<ChartData> chart = interactor.getCharts();
            ll.post(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < chart.size(); i++) {
                        renderChart(i + 1, chart.get(i));
                    }
                    applyTheme(getCurrentTheme());
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void applyTheme(Theme theme) {
        super.applyTheme(theme);
        ll.setBackgroundColor(getCurrentTheme().getBackgroundWindowColor());
        for (int i = 0; i < ll.getChildCount(); i++) {
            View child = ll.getChildAt(i);
            if (DIVIDER_TAG.equals(child.getTag())) {
                child.setBackgroundColor(getCurrentTheme().getDividerColor());
            }
            if (SPACING_TAG.equals(child.getTag())) {
                child.setBackgroundColor(getCurrentTheme().getBackgroundSpacingColor());
            }
            if (child instanceof CheckBox) {
                ((CheckBox) child).setTextColor(getCurrentTheme().getNameColor());
            }
        }
        for (Themable themable: themables) {
            if (themable != null) {
                themable.applyTheme(getCurrentTheme());
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_night_mode:
                toggleNightMode();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final String DIVIDER_TAG = "divider";
    private static final String SPACING_TAG = "spacing";
}
