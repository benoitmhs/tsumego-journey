package com.example.database_generator.model

import kotlin.math.max
import kotlin.math.min

data class BoardSection(
    val xMin: Int,
    val yMin: Int,
    val xMax: Int,
    val yMax: Int,
) {
    constructor(cell: Cell) : this(
        xMin = cell.x,
        yMin = cell.y,
        xMax = cell.x,
        yMax = cell.y,
    )

    fun includeCell(cell: Cell): BoardSection = cell.let { (x, y) ->
        BoardSection(
            xMin = min(xMin, x),
            yMin = min(yMin, y),
            xMax = max(xMax, x),
            yMax = max(yMax, y),
        )
    }
}