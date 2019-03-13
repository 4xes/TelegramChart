package com.telegram.chart.extensions

import com.telegram.chart.data.Line


data class MaxMin(val max: Long, val min: Long)

fun LongArray.maxMin(): MaxMin? {
    if (isEmpty()) return null
    var min = this[0]
    var max = this[0]
    for (i in 1..lastIndex) {
        val e = this[i]
        if (max < e) max = e
        if (min > e) min = e
    }
    return MaxMin(max, min)
}

fun List<Line>.maxMin(): MaxMin? {
    if (isEmpty()) return null
    var min = this[0].minY
    var max = this[0].maxY
    for (i in 1..lastIndex) {
        val e = this[i]
        if (max < e.maxY) max = e.maxY
        if (min > e.minY) min = e.minY
    }
    return MaxMin(max, min)
}