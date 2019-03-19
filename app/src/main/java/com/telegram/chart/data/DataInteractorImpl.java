package com.telegram.chart.data;

import android.content.Context;

import com.telegram.chart.data.utils.AssetUtils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataInteractorImpl implements ChartsInteractor {

    private final Context context;

    public DataInteractorImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<ChartData> getCharts() throws Throwable {
        JSONArray jsonArray = AssetUtils.readJsonArray(context, DATA_JSON_PATH);
        return new LineDataJsonMapper().map(jsonArray);
    }

    @Override
    public ChartData getAllChartsInOne() throws Throwable {
        List<ChartData> chartDataList = getCharts();

        ArrayList<LineData> lines = new ArrayList<>();
        for (ChartData chartData: chartDataList) {
            lines.addAll(Arrays.asList(chartData.getLines()));
        }

        return new ChartData(lines.toArray(new LineData[0]), chartDataList.get(0).getX());
    }

    private static final String DATA_JSON_PATH = "chart_data.json";
}