package com.mrsanglier.tsumegohero.dashboard.screens.collection

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
object CollectionDestination : THDestination {
    fun composable(
        navGraphBuilder: NavGraphBuilder,
    ) {
        navGraphBuilder.composable<CollectionDestination> {
            CollectionRoute()
        }
    }
}
