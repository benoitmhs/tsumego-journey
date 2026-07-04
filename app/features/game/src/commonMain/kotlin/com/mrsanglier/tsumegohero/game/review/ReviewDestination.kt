package com.mrsanglier.tsumegohero.game.review

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import com.mrsanglier.tsumegohero.game.model.BoardConfig
import com.mrsanglier.tsumegohero.game.model.Rotation
import kotlinx.serialization.Serializable

@Serializable
data class ReviewDestination(
    val tsumegoId: String,
    val rotation: Rotation?,
    val changeColor: Boolean?,
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
