package com.telegram.chart.view.range

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Region
import android.os.Build
import android.util.AttributeSet

class ChartRangeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : BaseRangeView(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        alpha = 40
    }

    private val touchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        alpha = 200
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val save = canvas.saveCount
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            canvas.clipOutRect(range)
        } else {
            @Suppress("DEPRECATION")
            canvas.clipRect(range, Region.Op.DIFFERENCE)
        }
        canvas.drawRect(bound, backgroundPaint)
        canvas.drawRect(line, linePaint)
        canvas.drawRect(fingerLeft, touchPaint)
        canvas.drawRect(fingerRight, touchPaint)
        canvas.restoreToCount(save)
    }
}