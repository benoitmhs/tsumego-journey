package com.example.database_generator.model

enum class BoardSize(val size: Int) {
    X9(9),
    X13(13),
    X19(19);

    companion object {
        fun fromValue(value: Int): BoardSize? =
            BoardSize.entries.firstOrNull { it.size == value }
    }
}