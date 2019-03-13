package com.telegram.chart.data

class Chart(
        val lines: List<Line>,
        val x: LongArray,
        val maxY: Long,
        val minY: Long
)