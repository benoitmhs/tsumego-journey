package com.mrsanglier.tsumegohero.di

import com.mrsanglier.tsumegohero.AppViewModel
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogManager
import com.mrsanglier.tsumegohero.coreui.componants.alertdialog.AlertDialogViewModel
import com.mrsanglier.tsumegohero.coreui.componants.loading.LoadingManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarManager
import com.mrsanglier.tsumegohero.coreui.componants.snackbar.SnackbarViewModel
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.TopBannerManager
import com.mrsanglier.tsumegohero.coreui.componants.topbanner.TopBannerViewModel
import com.mrsanglier.tsumegohero.navigation.MainNavigationViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::getAppConfig)
    single<LoadingManager> { LoadingManager() }
    singleOf(::AlertDialogManager)
    singleOf(::SnackbarManager)
    singleOf(::TopBannerManager)

    viewModelOf(::AppViewModel)
    viewModelOf(::MainNavigationViewModel)
    viewModelOf(::SnackbarViewModel)
    viewModelOf(::TopBannerViewModel)
    viewModelOf(::AlertDialogViewModel)
}
