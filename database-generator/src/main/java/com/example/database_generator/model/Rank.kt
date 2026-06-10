package com.example.database_generator.model

enum class Rank(val rawValue: String) {
    `15K`("15K"),
    `15Kplus`("15K+"),

    `14K`("14K"),
    `14Kplus`("14K+"),

    `13K`("13K"),
    `13Kplus`("13K+"),

    `12K`("12K"),
    `12Kplus`("12K+"),

    `11K`("11K"),
    `11Kplus`("11K+"),

    `10K`("10K"),
    `10Kplus`("10K+"),

    `9K`("9K"),
    `9Kplus`("9K+"),

    `8K`("8K"),
    `8Kplus`("8K+"),

    `7K`("7K"),
    `7Kplus`("7K+"),

    `6K`("6K"),
    `6Kplus`("6K+"),

    `5K`("5K"),
    `5Kplus`("5K+"),

    `4K`("4K"),
    `4Kplus`("4K+"),

    `3K`("3K"),
    `3Kplus`("3K+"),

    `2K`("2K"),
    `2Kplus`("2K+"),

    `1K`("1K"),
    `1Kplus`("1K+"),

    `1D`("1D"),
    `1Dplus`("1D+"),

    `2D`("2D"),
    `2Dplus`("2D+"),

    `3D`("3D"),
    `3Dplus`("3D+"),

    `4D`("4D"),
    `4Dplus`("4D+"),

    `5D`("5D"),
    `5Dplus`("5D+"),

    `6D`("6D"),
    `6Dplus`("6D+"),

    `7D`("7D"),
    `7Dplus`("7D+");

    companion object {
        val comparator: Comparator<Rank> = compareBy { entries.indexOf(it) }

        fun safeValueOf(raw: String): Rank? =
            entries.firstOrNull { it.rawValue == raw }
    }
}