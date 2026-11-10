package com.mrsanglier.tsumegohero.dashboard.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mrsanglier.tsumegohero.collection.list.CollectionListDestination
import com.mrsanglier.tsumegohero.collection.list.CollectionListNavScope
import com.mrsanglier.tsumegohero.coreui.animation.fadeInEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeInPopEnterTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeOutExitTransition
import com.mrsanglier.tsumegohero.coreui.animation.fadeOutPopExitTransition
import com.mrsanglier.tsumegohero.coreui.componants.modalbottomsheet.Composable
import com.mrsanglier.tsumegohero.coreui.componants.navigationbar.THNavigationBarScaffold
import com.mrsanglier.tsumegohero.coreui.componants.screen.ProvideScreenContext
import com.mrsanglier.tsumegohero.dashboard.screens.home.HomeDestination
import com.mrsanglier.tsumegohero.dashboard.screens.home.HomeNavScope
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardRoute(
    navScope: DashboardNavScope,
    viewModel: DashboardViewModel = koinViewModel(),
) {
    val navController = rememberNavController()
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val bottomSheet by viewModel.bottomSheet.collectAsStateWithLifecycle()

    val items = remember(navController, currentDestination) {
        listOf(
            DashboardNavItem.Play,
            DashboardNavItem.Collection,
        ).map {
            it.getNavigationBarItemData(
                navController = navController,
                currentDestination = currentDestination,
            )
        }
    }

    THNavigationBarScaffold(
        navigationItems = items,
        topBar = {},
    ) { paddings ->

        ProvideScreenContext(
            paddingValues = paddings,
        ) {
            NavHost(
                navController = navController,
                startDestination = HomeDestination,
                // The navigation bar already handles the system navigation bar inset
                modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),
                enterTransition = fadeInEnterTransition,
                exitTransition = fadeOutExitTransition,
                popEnterTransition = fadeInPopEnterTransition,
                popExitTransition = fadeOutPopExitTransition,
            ) {
                HomeDestination.composable(
                    navGraphBuilder = this,
                    navScope = HomeNavScope(
                        navigateToTraining = navScope.navigateToTraining,
                        navigateToRankEstimation = navScope.navigateToRankEstimation,
                    )
                )
                CollectionListDestination.composable(
                    navGraphBuilder = this,
                    navScope = CollectionListNavScope(
                        navigateToCollection = navScope.navigateToCollection,
                    ),
                )
            }
        }
    }

    bottomSheet?.Composable()
}
