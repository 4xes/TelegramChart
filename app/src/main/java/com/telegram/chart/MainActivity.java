package com.telegram.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.ChartsInteractor;
import com.telegram.chart.data.DataInteractorImpl;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.chart.ChartView;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.range.BaseRangeView;
import com.telegram.chart.view.range.BaseRangeView.OnRangeListener;
import com.telegram.chart.view.range.RangeView;

public class MainActivity extends ThemeBaseActivity {

    private ChartView chartView;
    private PreviewChartView previewView;
    private RangeView rangeView;
    private View divider;
    private View secondBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.chart_title);
        initViews();
        updateTheme();

        loadChart();
    }

    private void initViews() {
        chartView = findViewById(R.id.chart);
        previewView = findViewById(R.id.preview);
        divider = findViewById(R.id.divider);
        secondBackground = findViewById(R.id.secondaryBackground);
        rangeView = findViewById(R.id.range);

        chartView.setVisible(rangeView.getStart(), rangeView.getEnd(), false);
        rangeView.setOnRangeListener(new OnRangeListener() {
            @Override
            public void onChangeRange(Float start, Float end) {
                chartView.setVisible(start, end, false);
            }
        });
    }

    private void renderChart(ChartData chart) {
        chartView.setChartData(chart);
        previewView.setChartData(chart);
    }

    private void loadChart() {
        ChartsInteractor interactor = new DataInteractorImpl(getApplicationContext());
        ChartData chart = null;
        try {
            chart = interactor.getCharts().get(4);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        renderChart(chart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void toggleNightMode() {
        super.toggleNightMode();
        updateTheme();
    }

    private void updateTheme() {
        Theme theme = Theme.createTheme(this, isNightMode() ? Theme.NIGHT : Theme.DAY);
        applyTheme(theme);
        divider.setBackgroundColor(theme.getDividerColor());
        secondBackground.setBackgroundColor(theme.getBackgroundSecondColor());
        rangeView.applyTheme(theme);
        chartView.applyTheme(theme);
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
}
