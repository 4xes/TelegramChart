package com.telegram.chart;

import android.content.res.ColorStateList;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.CompoundButtonCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

import com.telegram.chart.data.ChartData;
import com.telegram.chart.data.ChartsInteractor;
import com.telegram.chart.data.DataInteractorImpl;
import com.telegram.chart.view.base.Theme;
import com.telegram.chart.view.chart.ChartView;
import com.telegram.chart.view.chart.Graph;
import com.telegram.chart.view.chart.InfoView;
import com.telegram.chart.view.chart.PreviewChartView;
import com.telegram.chart.view.range.BaseRangeView;
import com.telegram.chart.view.range.RangeView;

public class MainActivity extends ThemeBaseActivity {

    private ChartView chartView;
    private PreviewChartView previewView;
    private RangeView rangeView;
    private InfoView infoView;
    private View divider;
    private View secondBackground;
    private LinearLayout main;

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
        main = findViewById(R.id.main);
        chartView = findViewById(R.id.chart);
        previewView = findViewById(R.id.preview);
        divider = findViewById(R.id.divider);
        secondBackground = findViewById(R.id.secondaryBackground);
        rangeView = findViewById(R.id.range);
        infoView = findViewById(R.id.info);
    }

    private void initCheckboxes(final Graph graph) {
        for (int id = 0; id < graph.size(); id++) {
            AppCompatCheckBox checkBox = new AppCompatCheckBox(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int padding = getResources().getDimensionPixelOffset(R.dimen.normal);
            checkBox.setLayoutParams(params);
            checkBox.setPadding(padding, padding, padding, padding);
            checkBox.setText(graph.getName(id));
            checkBox.setTextColor(graph.getColor(id));
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
            main.addView(checkBox, main.indexOfChild(divider));
        }
    }

    private void renderChart(ChartData chart) {
        final Graph graph = new Graph(chart);
        rangeView.setOnRangeListener(new BaseRangeView.OnRangeListener() {
            @Override
            public void onChangeRange(Float start, Float end) {
                chartView.resetIndex();
                infoView.hide();
                graph.setStartAndMin(start, end);
            }
        });
        chartView.seGraph(graph);
        previewView.seGraph(graph);
        rangeView.seGraph(graph);
        infoView.seGraph(graph);
        chartView.setOnShowInfoListener(new ChartView.OnShowInfoListener() {
            @Override
            public void showInfo(int index, RectF bound, PointF point) {
                infoView.showInfo(index, bound, point);
            }
        });
        initCheckboxes(graph);
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
        Theme theme = Theme.createTheme(this, !isNightMode() ? Theme.NIGHT : Theme.DAY);
        applyTheme(theme);
        divider.setBackgroundColor(theme.getDividerColor());
        secondBackground.setBackgroundColor(theme.getBackgroundSecondColor());
        rangeView.applyTheme(theme);
        chartView.applyTheme(theme);
        infoView.applyTheme(theme);
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
