package com.telegram.chart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.telegram.chart.data.ChartInteractorImpl
import com.telegram.chart.data.ChartsInteractor
import com.telegram.chart.test.LineChartView
import kotlinx.android.synthetic.main.activity_test.*


class MainActivity : AppCompatActivity() {

    private val chartInteractor: ChartsInteractor by lazy {
        ChartInteractorImpl(this.applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chartInteractor.getCharts()
    }

}
