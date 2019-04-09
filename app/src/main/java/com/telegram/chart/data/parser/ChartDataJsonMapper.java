package com.telegram.chart.data.parser;

import android.graphics.Color;

import com.telegram.chart.data.Chart;
import com.telegram.chart.data.Data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

class ChartDataJsonMapper implements Mapper<Chart, JSONObject> {

    private static final String TYPES = "types";
    private static final String COLUMNS = "columns";
    private static final String NAMES = "names";
    private static final String COLORS = "colors";
    private static final String LINE = "line";
    private static final String BAR = "bar";
    private static final String Y_SCALED = "y_scaled";
    private static final String STACKED = "stacked";
    private static final String X = "x";

    @Override
    public Chart map(JSONObject json) throws Throwable {
        final JSONObject types = json.getJSONObject(TYPES);
        final JSONArray columns = json.getJSONArray(COLUMNS);
        final JSONObject names = json.getJSONObject(NAMES);
        final JSONObject colors = json.getJSONObject(COLORS);
        final boolean yScaled = json.optBoolean(Y_SCALED, false);
        final boolean stacked = json.optBoolean(STACKED, false);
        String typeChart = Chart.TYPE_LINE;

        final ArrayList<Data> lines = new ArrayList<>();
        int[] xPoints = new int[0];

        for (int ci = 0; ci < columns.length(); ci++) {
            final JSONArray columnJson = columns.getJSONArray(ci);
            if (columnJson.length() < 2) {
                continue;
            }
            final String key = columnJson.getString(0);
            final String type = types.getString(key);
            switch (type) {
                case X:
                    xPoints = new int[columnJson.length() - 1];
                    for (int vi = 1; vi < columnJson.length(); vi++) {
                        xPoints[vi - 1] = (int) (columnJson.getLong(vi) / 1000L);
                    }
                    break;
                default:
                    if (BAR.equals(type)) {
                        typeChart = Chart.TYPE_BAR;
                    }
                    if (LINE.equals(type)) {
                        typeChart = Chart.TYPE_LINE;
                    }
                    final int[] yPoints = new int[columnJson.length() - 1];
                    int minY = Integer.MAX_VALUE;
                    int maxY = Integer.MIN_VALUE;
                    for (int vi = 1; vi < columnJson.length(); vi++) {
                        int point = columnJson.getInt(vi);
                        if (point < minY) {
                            minY = point;
                        }
                        if (point > maxY) {
                            maxY = point;
                        }
                        yPoints[vi - 1] = point;
                    }
                    lines.add(new Data(
                            names.getString(key),
                            Color.parseColor(colors.getString(key)),
                            yPoints,
                            maxY,
                            minY
                    ));
                    break;

            }
        }
        if (yScaled) {
            typeChart = Chart.TYPE_LINE_SCALED;
        }
        if (Chart.TYPE_BAR.equals(typeChart) && stacked) {
            typeChart = Chart.TYPE_BAR_STACKED;
        }
        return new Chart(xPoints, lines.toArray(new Data[0]), typeChart);
    }
}