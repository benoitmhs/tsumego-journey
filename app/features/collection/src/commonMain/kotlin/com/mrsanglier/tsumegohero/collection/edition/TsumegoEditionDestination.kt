package com.mrsanglier.tsumegohero.collection.edition

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

/** Manual entry of a new problem, added to the collection [collectionId] at the end of the flow. */
@Serializable
data class TsumegoEditionDestination(
    val collectionId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: TsumegoEditionNavScope,
        ) {
            navGraphBuilder.composable<TsumegoEditionDestination> {
                TsumegoEditionRoute(navScope)
            }
        }
    }
}

data class TsumegoEditionNavScope(
    val navigateBack: () -> Unit,
)
