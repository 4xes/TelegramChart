package com.telegram.chart.view.chart

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.telegram.chart.extensions.pxFromDp
import com.telegram.chart.extensions.reconcileSize

abstract class BaseChartView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Chart {

    protected val bound = RectF()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        val minWidth = 200.pxFromDp()
        val minHeight = 30.pxFromDp()

        val measuredWidth = reconcileSize(minWidth, widthMeasureSpec)
        val measuredHeight = reconcileSize(minHeight, heightMeasureSpec)

        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        bound.left = paddingLeft.toFloat()
        bound.top = paddingTop.toFloat()
        bound.right = width - paddingRight.toFloat()
        bound.bottom = height - paddingBottom.toFloat()
    }

}