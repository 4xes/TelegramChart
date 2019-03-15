package com.telegram.chart.data;


import android.graphics.Color;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

class LineDataJsonMapper implements Mapper<List<ChartData>,JSONArray> {

    @Override
    public List<ChartData> map(JSONArray json) throws Throwable {
        final ArrayList<ChartData> data = new ArrayList<>();

        for (int i = 0; i < json.length(); i++) {
            final JSONObject jsonObject = json.getJSONObject(i);

            final JSONObject types = jsonObject.getJSONObject(TYPES);
            final JSONArray columns = jsonObject.getJSONArray(COLUMNS);
            final JSONObject names = jsonObject.getJSONObject(NAMES);
            final JSONObject colors = jsonObject.getJSONObject(COLORS);

            final ArrayList<LineData> lines = new ArrayList<>();
            long[] xPoints = new long[0];

            for (int ci = 0; ci < columns.length(); ci++) {
                final JSONArray columnJson = columns.getJSONArray(ci);
                if (columnJson.length() < 2) {
                    continue;
                }
                final String key = columnJson.getString(0);
                switch (types.getString(key)) {
                    case TYPE_LINE:
                        final long[] yPoints = new long[columnJson.length() - 1];
                        long minY = Long.MAX_VALUE;
                        long maxY = Long.MIN_VALUE;
                        for (int vi = 1; vi < columnJson.length(); vi++) {
                            long point = columnJson.getLong(vi);
                            if (point < minY) {
                                minY = point;
                            }
                            if (point > maxY) {
                                maxY = point;
                            }
                            yPoints[vi - 1] = point;
                        }
                        lines.add(new LineData(
                                names.getString(key),
                                Color.parseColor(colors.getString(key)),
                                yPoints,
                                maxY,
                                minY
                        ));
                        break;
                    case TYPE_X:
                        xPoints = new long[columnJson.length() - 1];
                        for (int vi = 1; vi < columnJson.length(); vi++) {
                            xPoints[vi - 1] = columnJson.getLong(vi);
                        }
                        break;
                }
            }
            data.add(new ChartData(lines.toArray(new LineData[0]), xPoints));
        }

        return data;
    }

    private static final String TYPES = "types";
    private static final String COLUMNS = "columns";
    private static final String NAMES = "names";
    private static final String COLORS = "colors";
    private static final String TYPE_LINE = "line";
    private static final String TYPE_X = "x";
}