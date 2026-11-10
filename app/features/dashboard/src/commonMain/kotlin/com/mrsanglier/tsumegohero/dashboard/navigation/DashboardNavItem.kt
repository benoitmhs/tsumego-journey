package com.mrsanglier.tsumegohero.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mrsanglier.tsumegohero.app.coreui.resources.dashboard_navigationBarItem_collection
import com.mrsanglier.tsumegohero.app.coreui.resources.dashboard_navigationBarItem_play
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_book_open
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_book_open_filled
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_home
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_home_filled
import com.mrsanglier.tsumegohero.collection.list.CollectionListDestination
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.navigationbar.FONavigationBarItemData
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.match
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.navigation.THDestination
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.dashboard.screens.home.HomeDestination

sealed interface DashboardNavItem {
    val label: TextSpec
    val selectedIcon: IconSpec
    val unselectedIcon: IconSpec
    val destination: THDestination

    data object Play : DashboardNavItem {
        override val label: TextSpec = THString.dashboard_navigationBarItem_play.toTextSpec()
        override val selectedIcon: IconSpec = THDrawable.ic_home_filled.toIconSpec()
        override val unselectedIcon: IconSpec = THDrawable.ic_home.toIconSpec()
        override val destination: THDestination = HomeDestination
    }

    data object Collection : DashboardNavItem {
        override val label: TextSpec = THString.dashboard_navigationBarItem_collection.toTextSpec()
        override val selectedIcon: IconSpec = THDrawable.ic_book_open_filled.toIconSpec()
        override val unselectedIcon: IconSpec = THDrawable.ic_book_open.toIconSpec()
        override val destination: THDestination = CollectionListDestination
    }

    fun getNavigationBarItemData(
        navController: NavController,
        currentDestination: NavDestination?,
        onClick: () -> Unit = {},
    ): FONavigationBarItemData = FONavigationBarItemData(
        label = label,
        selectedIcon = selectedIcon,
        unselectedIcon = unselectedIcon,
        isSelected = currentDestination.match(destination::class),
        onClick = {
            if (!currentDestination.match(destination::class)) {
                onClick()
                navController.navigate(destination) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}
