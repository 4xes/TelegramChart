package com.telegram.chart.view.chart

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import com.telegram.chart.data.Chart

class SimpleChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseChartView(context, attrs, defStyleAttr) {

    var maxY: Long = 0L
    var minY: Long = 0L

    var renders: List<SimpleRenderLine> = emptyList()

    fun setChart(chart: Chart) {
        maxY = chart.maxY
        minY = chart.minY
        renders = chart.lines.map {
            SimpleRenderLine(it)
        }
        computeRenders()
    }

    private fun computeRenders() {
        if (bound.width() > 0) {
            for (render in renders) {
                render.calculatePath(bound, maxY, minY)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        computeRenders()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (render in renders) {
            render.draw(canvas)
        }
    }

}