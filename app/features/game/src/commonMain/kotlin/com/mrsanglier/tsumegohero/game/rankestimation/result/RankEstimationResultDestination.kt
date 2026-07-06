package com.mrsanglier.tsumegohero.game.rankestimation.result

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class RankEstimationResultDestination(
    val rank: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: RankEstimationResultNavScope,
        ) {
            navGraphBuilder.composable<RankEstimationResultDestination> { entry ->
                RankEstimationResultRoute(
                    args = entry.toRoute(),
                    navScope = navScope,
                )
            }
        }
    }
}
