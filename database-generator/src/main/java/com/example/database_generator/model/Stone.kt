package com.example.database_generator.model

enum class Stone {
    BLACK, WHITE;

    companion object Companion {
        fun Stone.getOpponent(): Stone =
            if (this == BLACK) WHITE else BLACK
    }
}