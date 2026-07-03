package com.mrsanglier.tsumegohero.game.game

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class GameDestination(
    val tsumegoId: String,
    val isGhostMode: Boolean = true,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: GameNavScope,
        ) {
            navGraphBuilder.composable<GameDestination> {
                GameRoute(navScope)
            }
        }
    }
}
