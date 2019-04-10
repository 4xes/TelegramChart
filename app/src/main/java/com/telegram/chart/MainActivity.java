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

import com.telegram.chart.data.Chart;
import com.telegram.chart.data.parser.ChartsInteractor;
import com.telegram.chart.data.parser.DataInteractorImpl;
import com.telegram.chart.view.CheckboxesView;
import com.telegram.chart.view.annotation.Nullable;
import com.telegram.chart.view.chart.ChartView;
import com.telegram.chart.view.chart.GraphManager;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.chart.TooltipView;
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

    private void renderCheckboxes(final GraphManager graphManager, final TooltipView tooltipView) {
        CheckboxesView checkboxesView = new CheckboxesView(this);
        checkboxesView.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        checkboxesView.init(graphManager, (id, isVisible) -> {
            graphManager.setVisible(id, isVisible);
            if (tooltipView.isShowing()) {
                tooltipView.invalidate();
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

    private void renderChart(int number, final Chart chart) {
        final GraphManager graphManager = new GraphManager(chart);
        LayoutInflater li = LayoutInflater.from(this);
        final View chartWrapper = li.inflate(R.layout.chart_layout, content, false);
        final View previewWrapper = li.inflate(R.layout.preview_layout, content, false);
        final TooltipView tooltipView = chartWrapper.findViewById(R.id.info);
        final ChartView chartView = chartWrapper.findViewById(R.id.chart);
        final RangeView rangeView = previewWrapper.findViewById(R.id.range);
        final PreviewChartView previewChartView = previewWrapper.findViewById(R.id.preview);
        chartView.setTitleText(getString(R.string.chart_title, number));
        chartViews.add(chartView);
        themables.add(tooltipView);
        themables.add(chartView);
        themables.add(rangeView);
        themables.add(previewChartView);
        content.addView(chartWrapper);
        content.addView(previewWrapper);
        renderCheckboxes(graphManager, tooltipView);
        renderDivider(ViewUtils.pxFromDp(1), DIVIDER_TAG);
        renderDivider(ViewUtils.pxFromDp(40), SPACING_TAG);
        chartView.seGraph(graphManager);
        previewChartView.setGraph(graphManager);
        rangeView.seGraph(graphManager);
        tooltipView.seGraph(graphManager);
        rangeView.setOnRangeListener((start, end) -> {
            chartView.resetIndex();
            tooltipView.hideInfo();
           graphManager.update(rangeView.getViewId(), start, end);
        });
        chartView.setOnShowInfoListener(tooltipView);
    }

    private void loadChart() {
        ChartsInteractor interactor = new DataInteractorImpl(getApplicationContext());
        try {
            final Chart chart1 = interactor.getChart(1);
            final Chart chart2 = interactor.getChart(2);
            final Chart chart3 = interactor.getChart(3);
            final Chart chart4 = interactor.getChart(4);
            content.post(() -> {
                renderDivider(ViewUtils.pxFromDp(28), SPACING_TAG);
                renderChart(4, chart4);
                renderChart(3, chart3);
                renderChart(1, chart1);
                renderChart(2, chart2);
                applyTheme(getCurrentTheme());
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
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
