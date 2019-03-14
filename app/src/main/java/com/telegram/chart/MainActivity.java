package com.telegram.chart;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.telegram.chart.data.Chart;
import com.telegram.chart.data.ChartInteractorImpl;
import com.telegram.chart.data.ChartsInteractor;
import com.telegram.chart.view.chart.SimpleChartView;

public class MainActivity extends AppCompatActivity {

    private SimpleChartView chartView;
    private SimpleChartView previewView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
}
