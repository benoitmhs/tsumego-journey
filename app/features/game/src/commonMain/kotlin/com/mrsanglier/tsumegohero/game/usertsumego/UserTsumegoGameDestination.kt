package com.mrsanglier.tsumegohero.game.usertsumego

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

/** Plays a problem of a user collection, with the review available from the start. */
@Serializable
data class UserTsumegoGameDestination(
    val collectionId: String,
    val tsumegoId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: UserTsumegoGameNavScope,
        ) {
            navGraphBuilder.composable<UserTsumegoGameDestination> {
                UserTsumegoGameRoute(navScope)
            }
        }
    }
}
