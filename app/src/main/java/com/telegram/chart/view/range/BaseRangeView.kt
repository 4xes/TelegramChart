package com.telegram.chart.view.range

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.telegram.chart.extensions.pxFromDp
import com.telegram.chart.extensions.reconcileSize

abstract class BaseRangeView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    protected val bound = RectF()
    protected val line = RectF()
    protected val range = RectF()
    protected val fingerLeft = RectF()
    protected val fingerRight = RectF()

    private var xDown: Float = 0f

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
        range.set(line)
        fingerLeft.set(line)
        fingerRight.set(line)
        recalculateRange()
    }

    private fun recalculateRange() {
        range.left = line.left + (line.width() * start)
        range.right = line.left + (line.width() * end)

        fingerLeft.left = range.left - halfTouch
        fingerLeft.right = range.left + halfTouch


        fingerRight.left = range.right - halfTouch
        fingerRight.right = range.right + halfTouch
    }

    private var currentZone = Zone.None

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                currentZone = getZone(x)
                xDown = x
                return true
            }
            MotionEvent.ACTION_UP -> {

            }
            MotionEvent.ACTION_MOVE -> {
                val dx = x - xDown
                xDown = x
                when (currentZone) {
                    Zone.None -> {
                        //todo move to here by center rangeX
                        return true
                    }
                    Zone.Start -> {
                        moveStart(dx)
                    }
                    Zone.End -> {
                        moveEnd(dx)
                    }
                    Zone.Range -> {
                        moveRange(dx)
                    }

                }
            }
        }

        return false
    }

    private fun moveStart(dx: Float) {
       val dPercentage = dx / line.width()
       start += dPercentage
       if (start < 0f) {
           start = 0f
       } else if (end - start < min) {
           start = end - min
       }
       recalculateRange()
       invalidate()
    }

    private fun moveEnd(dx: Float) {
        val dPercentage = dx / line.width()
        end += dPercentage
        if (end > 1f) {
            end = 1f
        } else if (end - start < min) {
            if (start + min > 1f) {
                start = 1f - min
                end = 1f
            } else {
                end = start + min
            }
        }
        recalculateRange()
        invalidate()
    }

    private fun moveRange(dx: Float) {
        val dPercentage = dx / line.width()
        val range = end - start

        val toRight = dPercentage > 0
        if (toRight) {
            end += dPercentage
            if (end > 1f) {
                end = 1f
            }
            start = end - range
        } else {
            start += dPercentage
            if (start < 0f) {
                start = 0f
            }
            end = start + range
        }
        recalculateRange()
        invalidate()
    }

    private fun getZone(x: Float): Zone {
        if (fingerLeft.containsX(x)) {
            return Zone.Start
        }
        if (fingerRight.containsX(x)) {
            return Zone.End
        }
        if (range.containsX(x)) {
            return Zone.Range
        }
        return Zone.None
    }

    fun setValues(start: Float, end: Float) {
        this.start = start
        this.end = end
        invalidate()
    }

    private fun RectF.containsX(value: Float): Boolean = value in left..right

}