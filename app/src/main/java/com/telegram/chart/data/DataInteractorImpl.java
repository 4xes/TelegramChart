package com.telegram.chart.data;

import android.content.Context;

import com.telegram.chart.data.utils.AssetUtils;

import org.json.JSONArray;

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

    private static final String DATA_JSON_PATH = "chart_data.json";
}