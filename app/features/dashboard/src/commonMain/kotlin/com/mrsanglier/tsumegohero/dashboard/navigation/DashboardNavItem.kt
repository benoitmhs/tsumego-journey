package com.mrsanglier.tsumegohero.dashboard.navigation

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.mrsanglier.tsumegohero.app.coreui.resources.dashboard_navigationBarItem_play
import com.mrsanglier.tsumegohero.app.coreui.resources.dashboard_navigationBarItem_profile
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_home
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_home_filled
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_profile
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_profile_filled
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
import com.mrsanglier.tsumegohero.dashboard.screens.profile.ProfileDestination

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

    data object Profile : DashboardNavItem {
        override val label: TextSpec = THString.dashboard_navigationBarItem_profile.toTextSpec()
        override val selectedIcon: IconSpec = THDrawable.ic_profile_filled.toIconSpec()
        override val unselectedIcon: IconSpec = THDrawable.ic_profile.toIconSpec()
        override val destination: THDestination = ProfileDestination
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
