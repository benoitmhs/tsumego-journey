package com.mrsanglier.tsumegohero.game.training

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode
import kotlinx.serialization.Serializable

@Serializable
data class TrainingDestination(
    val tsumegoId: String,
    val autoPlay: Boolean = false,
    val ghost: Boolean = true,
    val trainingMode: TrainingMode,
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
