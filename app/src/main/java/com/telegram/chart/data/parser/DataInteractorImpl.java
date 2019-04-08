package com.telegram.chart.data.parser;

import android.content.Context;

import com.telegram.chart.data.Chart;

import org.json.JSONObject;

public class DataInteractorImpl implements ChartsInteractor {

    private final Context context;

    public DataInteractorImpl(Context context) {
        this.context = context;
    }

    @Override
    public Chart getChart(int number) throws Throwable {
        JSONObject jsonObject = AssetUtils.readJsonObject(context, number + "/overview.json");
        return new ChartDataJsonMapper().map(jsonObject);
    }

}