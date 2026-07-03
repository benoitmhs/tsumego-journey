package com.mrsanglier.tsumegohero.game.game

import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_forward
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_next
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_previous
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_refresh
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable

internal val GameViewModel.defaultRestartButton: THButtonState
    get() = THButtonState(
        text = "Restart".toTextSpec(), // TODO: loco
        style = ButtonStyle.Secondary,
        onClick = ::reset,
    )

internal val GameViewModel.defaultNextButton: THButtonState
    get() = THButtonState(
        text = null,
        trailingIcon = THDrawable.ic_arrow_forward.toIconSpec(),
        style = ButtonStyle.Secondary,
        onClick = ::next,
    )

internal val GameViewModel.defaultReviewButton: THButtonState
    get() = THButtonState(
        text = "📖 Review".toTextSpec(), // TODO: loco
        style = ButtonStyle.Secondary,
        onClick = ::startReview,
    )

internal val GameViewModel.defaultReviewPreviousButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = { navigateReview(true) },
        icon = THDrawable.ic_previous.toIconSpec(),
    )

internal val GameViewModel.defaultReviewNextButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = { navigateReview(false) },
        icon = THDrawable.ic_next.toIconSpec(),
    )

internal val GameViewModel.defaultReviewResetButton: THButtonState
    get() = THButtonState(
        text = null,
        style = ButtonStyle.Text,
        onClick = ::startReview,
        icon = THDrawable.ic_refresh.toIconSpec(),
    )