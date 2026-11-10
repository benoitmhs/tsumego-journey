package com.mrsanglier.tsumegohero.problemedition.edit

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

/** Edition of the saved problem [tsumegoId], every step reachable at any time. */
@Serializable
data class EditProblemDestination(
    val collectionId: String,
    val tsumegoId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: EditProblemNavScope,
        ) {
            navGraphBuilder.composable<EditProblemDestination> {
                EditProblemRoute(navScope)
            }
        }
    }
}

data class EditProblemNavScope(
    val navigateBack: () -> Unit,
)
