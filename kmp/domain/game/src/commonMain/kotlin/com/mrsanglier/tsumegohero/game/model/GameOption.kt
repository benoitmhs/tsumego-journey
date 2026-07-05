package com.mrsanglier.tsumegohero.game.model

/**
 * @property autoPlay: true the player plays one color and opponent moves are played automatically,
 * false the player plays both colors and submits the result manually
 * @property ghost: true played moves do not appear on the board until the result is revealed,
 * false moves stay visible
 */
data class GameOption(
    val autoPlay: Boolean,
    val ghost: Boolean,
)
