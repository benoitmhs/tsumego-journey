package com.mrsanglier.tsumegohero.collection.detail

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
data class CollectionDetailDestination(
    val collectionId: String,
) : THDestination {

    companion object {
        fun composable(
            navGraphBuilder: NavGraphBuilder,
            navScope: CollectionDetailNavScope,
        ) {
            navGraphBuilder.composable<CollectionDetailDestination> {
                CollectionDetailRoute(navScope)
            }
        }
    }
}

data class CollectionDetailNavScope(
    val navigateBack: () -> Unit,
    val navigateToTsumegoEdition: (collectionId: String) -> Unit,
)
