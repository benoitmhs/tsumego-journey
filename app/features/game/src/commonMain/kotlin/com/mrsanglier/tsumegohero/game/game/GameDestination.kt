package com.mrsanglier.tsumegohero.game.game

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import com.mrsanglier.tsumegohero.data.model.game.GameContext
import com.mrsanglier.tsumegohero.data.model.game.GameMode
import kotlinx.serialization.Serializable

@Serializable
data class GameDestination(
    val tsumegoId: String,
    val gameMode: GameMode = GameMode.Ghost,
    val context: GameContext = GameContext.Training,
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
