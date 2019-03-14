package com.telegram.chart

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.telegram.chart.data.ChartInteractorImpl
import com.telegram.chart.data.ChartsInteractor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val chartInteractor: ChartsInteractor by lazy {
        ChartInteractorImpl(this.applicationContext)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val chart0 = chartInteractor.getCharts()[0]
        chart.setChart(chart0)
        preview.setChart(chart0)

    }


}
