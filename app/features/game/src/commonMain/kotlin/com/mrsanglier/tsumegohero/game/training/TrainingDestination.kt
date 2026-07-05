package com.mrsanglier.tsumegohero.game.training

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class TrainingDestination(
    val tsumegoId: String,
    val autoPlay: Boolean = false,
    val ghost: Boolean = true,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: TrainingNavScope,
        ) {
            navGraphBuilder.composable<TrainingDestination> {
                TrainingRoute(navScope)
            }
        }
    }
}
