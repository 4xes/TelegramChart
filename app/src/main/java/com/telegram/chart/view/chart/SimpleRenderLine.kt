package com.telegram.chart.view.chart

import android.graphics.*
import com.telegram.chart.data.Line
import com.telegram.chart.extensions.pxFromDp

class SimpleRenderLine(val line: Line) {

    private val path = Path()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = line.color
        style = Paint.Style.STROKE
        strokeWidth = 1f.pxFromDp()
    }

    private val matrix = Matrix()

    fun calculatePath(bound: RectF, maxY: Long, minY: Long) {

        matrix.setScale(1f, 1f, bound.centerX(), bound.centerY())
        if (line.y.size > 2) {
            val dx = bound.width() / (line.y.size - 1)
            val scaleY = (maxY - minY) / bound.height()
            path.reset()
            path.moveTo(bound.left, bound.bottom - (line.y[0] * scaleY))
            for ((i, y) in line.y.withIndex()) {
                path.lineTo(bound.left + i * dx, bound.bottom - ((y - minY) / scaleY))
            }
        }
        path.transform(matrix)
    }

    fun draw(canvas: Canvas) {
        canvas.drawPath(path, paint)
    }


}