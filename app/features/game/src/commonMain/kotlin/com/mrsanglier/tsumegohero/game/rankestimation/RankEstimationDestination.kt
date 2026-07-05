package com.mrsanglier.tsumegohero.game.rankestimation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class RankEstimationDestination(
    val tsumegoId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: RankEstimationNavScope,
        ) {
            navGraphBuilder.composable<RankEstimationDestination> {
                RankEstimationRoute(navScope)
            }
        }
    }
}
