package com.example.database_generator.model

data class CropBoard(
    val boardSection: BoardSection,
    val corner: Corner,
)

enum class Corner {
    TopLeft, TopRight, BottomLeft, BottomRight;
}