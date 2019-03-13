package com.telegram.chart.data

import android.graphics.Color
import android.support.v4.util.ArrayMap
import com.telegram.chart.extensions.maxMin
import org.json.JSONArray
import org.json.JSONObject

class ChartJsonMapper: Mapper<List<Chart>, JSONArray> {
    override fun map(array: JSONArray): List<Chart> {
        val charts = mutableListOf<Chart>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            charts.add(parseChart(obj))
        }
        return charts
    }

    private fun parseChart(obj: JSONObject): Chart {
        val types = obj.getJSONObject("types")
        val columns = obj.getJSONArray("columns")
        val names = obj.getJSONObject("names")
        val colors = obj.getJSONObject("colors")

        val data = parseData(columns)
        val lines = mutableListOf<Line>()
        var xKey = "x"
        for (key in types.keys()) {
            val type = types.getString(key)
            when (type) {
                "line" -> {
                    val y = data.getValue(key)
                    val (max, min) = y.maxMin()!!

                    lines.add(Line(
                            name = names.getString(key),
                            color = Color.parseColor(colors.getString(key)),
                            y = y,
                            maxY = max,
                            minY = min
                    ))
                }
                "x" -> {
                    xKey = key
                }
            }
        }
        val xData = data.getValue(xKey)
        val (max, min) = lines.maxMin()!!
        return Chart(lines, xData, max, min)
    }

    private fun parseData(columns: JSONArray): Map<String, LongArray> {
        val data = ArrayMap<String, LongArray>()
        for (i in 0 until columns.length()) {
            val column = columns.getJSONArray(i)
            data[column.getString(0)] = LongArray(column.length() - 1) { index ->
                column.getLong(index + 1)
            }
        }
        return data
    }
}