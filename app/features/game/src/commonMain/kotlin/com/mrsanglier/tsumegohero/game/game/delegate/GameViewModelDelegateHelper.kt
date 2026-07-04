package com.mrsanglier.tsumegohero.game.game.delegate

import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_forward
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_refresh
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButtonState
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable

internal fun defaultRestartButton(onClick: () -> Unit): THButtonState = THButtonState(
    text = "Restart".toTextSpec(), // TODO: loco
    style = ButtonStyle.Text,
    onClick = onClick,
    icon = THDrawable.ic_refresh.toIconSpec(),
)

internal fun defaultNextButton(onClick: () -> Unit): THButtonState = THButtonState(
    text = null,
    trailingIcon = THDrawable.ic_arrow_forward.toIconSpec(),
    style = ButtonStyle.Secondary,
    onClick = onClick,
)

internal fun defaultSubmitButton(onClick: () -> Unit): THButtonState = THButtonState(
    text = "Submit".toTextSpec(), // TODO: loco
    style = ButtonStyle.Primary,
    onClick = onClick,
)

internal fun defaultSkipButton(onClick: () -> Unit): THButtonState = THButtonState(
    text = "Skip".toTextSpec(), // TODO: loco
    style = ButtonStyle.Secondary,
    onClick = onClick,
)

internal fun defaultReviewButton(onClick: () -> Unit): THButtonState = THButtonState(
    text = "📖 Review".toTextSpec(), // TODO: loco
    style = ButtonStyle.Text,
    onClick = onClick,
)
