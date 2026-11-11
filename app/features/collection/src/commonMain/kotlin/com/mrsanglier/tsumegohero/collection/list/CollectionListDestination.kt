package com.mrsanglier.tsumegohero.collection.list

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
object CollectionListDestination : THDestination {
    fun composable(
        navGraphBuilder: NavGraphBuilder,
        navScope: CollectionListNavScope,
    ) {
        navGraphBuilder.composable<CollectionListDestination> {
            CollectionListRoute(navScope)
        }
    }
}

data class CollectionListNavScope(
    val navigateToCollection: (collectionId: String, collectionName: String) -> Unit,
)
