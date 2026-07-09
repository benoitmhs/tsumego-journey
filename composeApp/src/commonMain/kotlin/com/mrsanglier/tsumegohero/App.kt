package com.mrsanglier.tsumegohero

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogView
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogViewModel
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingView
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarView
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarViewModel
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.TopBannerView
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.TopBannerViewModel
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.maintenance.screen.ForceUpdateScreen
import com.mrsanglier.tsumegohero.maintenance.screen.MaintenanceScreen
import com.mrsanglier.tsumegohero.navigation.AuthenticationNavigation
import com.mrsanglier.tsumegohero.navigation.MainNavigation
import com.mrsanglier.tsumegohero.splashscreen.SplashScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun App(
    viewModel: AppViewModel = koinViewModel(),
) {
    val loadingManager = koinInject<LoadingManager>()
    val mainGraph by viewModel.mainGraph.collectAsStateWithLifecycle()

    THTheme {
        Box(
            modifier = Modifier.background(THTheme.colors.background),
        ) {
            Crossfade(mainGraph) { graph ->
                when (graph) {
                    is MainGraph.ForceUpdate -> ForceUpdateScreen()
                    is MainGraph.Maintenance -> MaintenanceScreen()
                    MainGraph.Init -> SplashScreen()
                    is MainGraph.Authenticated -> MainNavigation()
                    is MainGraph.NoAuthenticated -> AuthenticationNavigation()
                }
            }

            LoadingView(
                loadingManager = loadingManager,
                contentDescription = "Loading".toTextSpec(),
            )
            AlertDialogView(
                viewModel = koinViewModel<AlertDialogViewModel>(),
            )

            SnackbarView(
                viewModel = koinViewModel<SnackbarViewModel>(),
                modifier = Modifier.align(Alignment.BottomCenter),
            )

            TopBannerView(
                viewModel = koinViewModel<TopBannerViewModel>(),
                modifier = Modifier.align(Alignment.TopCenter),
            )
        }
    }
}
