package com.mrsanglier.tsumegohero.dashboard.screens.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import kotlinx.serialization.Serializable

@Serializable
object HomeDestination : THDestination {
    fun composable(
        navGraphBuilder: NavGraphBuilder,
        navScope: HomeNavScope,
    ) {
        navGraphBuilder.composable<HomeDestination> {
            HomeRoute(navScope)
        }
    }
}
