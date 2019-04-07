package com.telegram.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toolbar;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.view.CheckboxesView;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.ChartView;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.chart.InfoView;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.range.RangeView;
import com.telegram.chart.view.theme.Themable;
import com.telegram.chart.view.theme.Theme;
import com.telegram.chart.view.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class MainActivity extends ThemeBaseActivity {

    private LinearLayout content;
    private List<ChartView> chartViews = new ArrayList<>();
    private List<Themable> themables = new ArrayList<>();
    private Menu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            setActionBar(toolbar);
        }

        setTitle(R.string.app_title);
        initViews();
        applyTheme(getCurrentTheme());
        loadChart();
    }

    private void initViews() {
        content = findViewById(R.id.content);
    }

    private void renderCheckboxes(final Graph graph, final InfoView infoView) {
        CheckboxesView checkboxesView = new CheckboxesView(this);
        checkboxesView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        checkboxesView.init(graph, (id, isVisible) -> {
            graph.setVisible(id, isVisible);
            if (infoView.isShowing()) {
                infoView.invalidate();
            }
        });
        content.addView(checkboxesView);
        themables.add(checkboxesView);
    }

    private void renderDivider(int height, String tag) {
        View line = new View(this);
        line.setTag(tag);
        line.setLayoutParams(new LinearLayout.LayoutParams(
                MATCH_PARENT,
                height
        ));
        content.addView(line);
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
        final View chartWrapper = li.inflate(R.layout.chart_layout, content, false);
        final View previewWrapper = li.inflate(R.layout.preview_layout, content, false);
        final InfoView infoView = chartWrapper.findViewById(R.id.info);
        final ChartView chartView = chartWrapper.findViewById(R.id.chart);
        final RangeView rangeView = previewWrapper.findViewById(R.id.range);
        final PreviewChartView previewChartView = previewWrapper.findViewById(R.id.preview);
        chartView.setTitleText(getString(R.string.chart_title, number));
        chartViews.add(chartView);
        themables.add(infoView);
        themables.add(chartView);
        themables.add(rangeView);
        themables.add(previewChartView);
        content.addView(chartWrapper);
        content.addView(previewWrapper);
        renderCheckboxes(graph, infoView);
        renderDivider(ViewUtils.pxFromDp(1), DIVIDER_TAG);
        renderDivider(ViewUtils.pxFromDp(40), SPACING_TAG);
        chartView.seGraph(graph);
        previewChartView.setGraph(graph);
        rangeView.seGraph(graph);
        infoView.seGraph(graph);
        rangeView.setOnRangeListener((start, end) -> {
            chartView.resetIndex();
            infoView.hideInfo();
           graph.update(rangeView.getViewId(), start, end);
        });
        chartView.setOnShowInfoListener(infoView);
    }

    private void loadChart() {
        content.post(() -> {
            for (int i = 0; i < ChartData.charts.length; i++) {
                renderDivider(ViewUtils.pxFromDp(28), SPACING_TAG);
                renderChart(i + 1, ChartData.charts[i]);
            }
            applyTheme(getCurrentTheme());
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final MenuInflater inflater = getMenuInflater();
        final int resMenu = getCurrentTheme().getId() == Theme.DAY ? R.menu.main_day : R.menu.main_night;
        inflater.inflate(resMenu, menu);
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void applyTheme(Theme theme) {
        super.applyTheme(theme);
        for (int i = 0; i < content.getChildCount(); i++) {
            View child = content.getChildAt(i);
            if (child instanceof LinearLayout) {
                child.setBackgroundColor(getCurrentTheme().getBackgroundWindowColor());
            }
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
    protected void onDestroy() {
        super.onDestroy();
        chartViews.clear();
        themables.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_night_mode:
                toggleNightMode();
                invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final String DIVIDER_TAG = "divider";
    private static final String SPACING_TAG = "spacing";
}
