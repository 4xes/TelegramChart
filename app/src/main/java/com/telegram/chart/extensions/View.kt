package com.telegram.chart.extensions


import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.util.TypedValue
import android.view.View

fun Int.pxFromDp(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), Resources.getSystem().displayMetrics).toInt()
}


fun Int.pxFromSp(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, toFloat(), Resources.getSystem().displayMetrics).toInt()
}

fun Float.pxFromDp(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, toFloat(), Resources.getSystem().displayMetrics)
}


fun Float.pxFromSp(): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, toFloat(), Resources.getSystem().displayMetrics)
}

fun Context.getAccentColor(): Int {
    val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        android.R.attr.colorAccent
    } else {
        resources.getIdentifier("colorAccent", "attr", packageName)
    }
    val outValue = TypedValue()
    theme.resolveAttribute(colorAttr, outValue, true)
    return outValue.data
}


/**
 * Reconcile a desired size for the view contents with a [android.view.View.MeasureSpec]
 * constraint passed by the parent.
 *
 * This is a simplified version of [View.resolveSize]
 *
 * @param contentSize Size of the view's contents.
 * @param measureSpec A [android.view.View.MeasureSpec] passed by the parent.
 * @return A size that best fits `contentSize` while respecting the parent's constraints.
 */
fun View.reconcileSize(contentSize: Int, measureSpec: Int): Int {
    val mode = View.MeasureSpec.getMode(measureSpec)
    val specSize = View.MeasureSpec.getSize(measureSpec)

    return when (mode) {
        View.MeasureSpec.EXACTLY -> specSize
        View.MeasureSpec.AT_MOST -> if (contentSize < specSize) contentSize else specSize
        View.MeasureSpec.UNSPECIFIED -> contentSize
        else -> contentSize
    }
}