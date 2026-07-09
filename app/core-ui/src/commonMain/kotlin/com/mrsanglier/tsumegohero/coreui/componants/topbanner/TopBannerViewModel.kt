package com.mrsanglier.tsumegohero.coreui.componants.topbanner

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class TopBannerViewModel(
    private val topBannerManager: TopBannerManager,
) : ViewModel() {
    val shownBanner: StateFlow<THTopBannerState?> = topBannerManager.shownBanner
    fun consumeBanner(): Unit = topBannerManager.consume()
}
