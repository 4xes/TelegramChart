package com.telegram.chart.data

import android.content.Context
import org.json.JSONArray

class ChartInteractorImpl(private val context: Context): ChartsInteractor {

    private val jsonMapper = ChartJsonMapper()

    override fun getCharts(): List<Chart> {
        val json = context.assets.open("chart_data.json").bufferedReader().use {
            it.readText()
        }
        return jsonMapper.map(JSONArray(json))
    }
}