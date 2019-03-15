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
import com.telegram.chart.view.chart.SimpleChartView;
import com.telegram.chart.view.range.ChartRangeView;

public class MainActivity extends ThemeBaseActivity {

    private SimpleChartView chartView;
    private SimpleChartView previewView;
    private ChartRangeView rangeView;
    private View divider;
    private View secondBackground;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chartView = findViewById(R.id.chart);
        previewView = findViewById(R.id.preview);
        divider = findViewById(R.id.divider);
        secondBackground = findViewById(R.id.secondaryBackground);
        rangeView = findViewById(R.id.range);

        updateTheme();

        loadChart();
    }

    private void renderChart(ChartData chart) {
        chartView.setChartData(chart);
        previewView.setChartData(chart);
    }

    private void loadChart() {
        ChartsInteractor interactor = new DataInteractorImpl(getApplicationContext());
        ChartData chart = null;
        try {
            chart = interactor.getCharts().get(0);
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
