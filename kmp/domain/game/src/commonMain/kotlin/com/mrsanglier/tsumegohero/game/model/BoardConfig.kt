package com.mrsanglier.tsumegohero.game.model

data class BoardConfig(
    val rotation: Rotation,
    val changeColor: Boolean,
) {
    companion object {
        fun random(): BoardConfig = BoardConfig(
            rotation = Rotation.entries.random(),
            changeColor = setOf(true, false).random(),
        )
    }
}

enum class Rotation {
    `90`, `180`, `-90`, None;
}