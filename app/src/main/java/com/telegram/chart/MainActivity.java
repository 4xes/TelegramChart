package com.telegram.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.telegram.chart.data.Chart;
import com.telegram.chart.data.ChartInteractorImpl;
import com.telegram.chart.data.ChartsInteractor;
import com.telegram.chart.view.chart.SimpleChartView;

public class MainActivity extends AppCompatActivity {

    private SimpleChartView chartView;
    private SimpleChartView previewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
            setTheme(R.style.AppThemeDark);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chartView = findViewById(R.id.chart);
        previewView = findViewById(R.id.preview);

        loadChart();
    }

    private void renderChart(Chart chart) {
        chartView.setChart(chart);
        previewView.setChart(chart);
    }

    private void loadChart() {
        ChartsInteractor interactor = new ChartInteractorImpl(getApplicationContext());
        Chart chart = interactor.getCharts().get(0);
        renderChart(chart);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_night_mode:
                if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                } else {
                    AppCompatDelegate
                            .setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                //getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
