package com.telegram.chart.view.range.base

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.telegram.chart.extensions.pxFromDp
import com.telegram.chart.extensions.reconcileSize

class RangeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Range {

    private val bound = RectF()
    private val line = RectF()

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
    }

    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
    }

    private val rangePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
    }

    private val touchPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        alpha = 122
    }

    private var start = 0.8f
    private var end = 1f
    private var min = 0.2f

    private var halfTouch = 10f.pxFromDp()

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

        line.set(bound)
        line.inset(halfTouch, 0f)

    }

    private var currentZone = Zone.None

    override fun onTouchEvent(event: MotionEvent): Boolean {

        val x = event.x

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentZone = getZone(x)
            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {

            }
        }

        return false
    }

    private fun getZone(x: Float): Zone {
        val left = line.left + (line.width() * start)
        val right = line.left + (line.width() * end)

        if (x >= left + halfTouch && x <= right + halfTouch) {
            return Zone.Move
        }
        return Zone.None
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(bound, backgroundPaint)
        canvas.drawRect(line, linePaint)

        val left = line.left + (line.width() * start)
        val right = line.left + (line.width() * end)

        canvas.drawRect(left, line.top, right, line.bottom, rangePaint)
        canvas.drawRect(left - halfTouch, line.top, left + halfTouch, line.bottom, touchPaint)
        canvas.drawRect(right - halfTouch, line.top, right + halfTouch, line.bottom, touchPaint)
    }

    override fun setValues(start: Float, end: Float) {
        this.start = start
        this.end = end
        invalidate()
    }
}