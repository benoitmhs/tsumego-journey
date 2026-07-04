package com.mrsanglier.tsumegohero.game.review

import com.mrsanglier.tsumegohero.app.coreui.resources.ic_next
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_previous
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_refresh
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable

internal val ReviewViewModel.defaultReviewPreviousButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = { navigate(true) },
        icon = THDrawable.ic_previous.toIconSpec(),
    )

internal val ReviewViewModel.defaultReviewNextButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = { navigate(false) },
        icon = THDrawable.ic_next.toIconSpec(),
    )

internal val ReviewViewModel.defaultReviewResetButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = ::restart,
        icon = THDrawable.ic_refresh.toIconSpec(),
    )
