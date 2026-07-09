package com.mrsanglier.tsumegohero.game.promotion

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class LevelPromotionDestination(
    val rank: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: LevelPromotionNavScope,
        ) {
            navGraphBuilder.composable<LevelPromotionDestination> { entry ->
                LevelPromotionRoute(
                    args = entry.toRoute(),
                    navScope = navScope,
                )
            }
        }
    }
}
