package com.telegram.chart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ThemeBaseActivity {

    private LinearLayout content;
    private List<ChartView> chartViews = new ArrayList<>();
    private List<View> shadows = new ArrayList<>();
    private List<Themable> themables = new ArrayList<>();

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
        LayoutInflater inflater = LayoutInflater.from(this);
        final LinearLayout view = (LinearLayout) inflater.inflate(R.layout.chart_layout, content, false);
        final View shadowTop = view.findViewById(R.id.shadowTop);
        final View shadowBottom = view.findViewById(R.id.shadowBottom);
        final ChartView chartView = view.findViewById(R.id.chart);
        final TooltipView tooltipView = view.findViewById(R.id.info);
        final RangeView rangeView = view.findViewById(R.id.range);
        final PreviewChartView previewChartView = view.findViewById(R.id.preview);
        final CheckboxesView checkboxesView = view.findViewById(R.id.checkboxes);
        chartView.setTitleText(getString(R.string.chart_title, number));
        shadowTop.setTag(SHADOW_TOP);
        shadowBottom.setTag(SHADOW_BOTTOM);
        shadows.add(shadowTop);
        shadows.add(shadowBottom);
        chartViews.add(chartView);
        themables.add(tooltipView);
        themables.add(chartView);
        themables.add(rangeView);
        themables.add(previewChartView);
        themables.add(checkboxesView);
        content.addView(view);
        chartView.seGraph(graphManager);
        previewChartView.setGraph(graphManager);
        rangeView.seGraph(graphManager);
        tooltipView.seGraph(graphManager);
        checkboxesView.init(graphManager, (id, isVisible) -> {
            graphManager.setVisible(id, isVisible);
            if (tooltipView.isShowing()) {
                tooltipView.invalidate();
            }
        });
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
            final Chart chart5 = interactor.getChart(5);
            content.post(() -> {
                renderChart(5, chart5);
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
        content.setBackgroundColor(theme.backgroundSpacingColor);
        for (int i = 0; i < shadows.size(); i++) {
            final View shadow = shadows.get(i);
            if (shadow != null) {
                if (SHADOW_TOP.equals(shadow.getTag())) {
                    shadow.setBackgroundResource(theme.shadowTop);
                }
                if (SHADOW_BOTTOM.equals(shadow.getTag())) {
                    shadow.setBackgroundResource(theme.shadowBottom);
                }
            }

        }
        for (int i = 0; i < themables.size(); i++) {
            Themable themable = themables.get(i);
            if (themable != null) {
                themable.applyTheme(theme);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chartViews.clear();
        themables.clear();
        shadows.clear();
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

    private static final String SHADOW_TOP = "shadow_top";
    private static final String SHADOW_BOTTOM= "shadow_bottom";
}
