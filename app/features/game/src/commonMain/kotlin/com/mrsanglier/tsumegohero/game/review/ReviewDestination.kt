package com.mrsanglier.tsumegohero.game.review

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDestination(
    val tsumegoId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: ReviewNavScope,
        ) {
            navGraphBuilder.composable<ReviewDestination> {
                ReviewRoute(navScope)
            }
        }
    }
}
