package com.mrsanglier.tsumegohero.problemedition.create

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

/** Step by step entry of a new problem, added to the collection [collectionId] at the end of the flow. */
@Serializable
data class CreateProblemDestination(
    val collectionId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: CreateProblemNavScope,
        ) {
            navGraphBuilder.composable<CreateProblemDestination> {
                CreateProblemRoute(navScope)
            }
        }
    }
}

data class CreateProblemNavScope(
    val navigateBack: () -> Unit,
)
