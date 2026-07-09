package com.mrsanglier.tsumegohero.coreui.componants.topbanner

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TopBannerManager {
    private val queue = ArrayDeque<THTopBannerState>()

    private val _shownBanner: MutableStateFlow<THTopBannerState?> = MutableStateFlow(null)
    val shownBanner: StateFlow<THTopBannerState?> = _shownBanner.asStateFlow()

    // A banner sent while another one is displayed is queued and shown right after it
    fun show(state: THTopBannerState) {
        if (_shownBanner.value == null) {
            _shownBanner.value = state
        } else {
            queue.addLast(state)
        }
    }

    fun consume() {
        _shownBanner.value = queue.removeFirstOrNull()
    }
}
