package com.mrsanglier.tsumegohero.coreui.extension

import androidx.compose.foundation.Indication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.coreui.componants.background.BackgroundSpec
import com.mrsanglier.tsumegohero.coreui.componants.background.foBackground
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.color.Transparent
import com.mrsanglier.tsumegohero.coreui.utils.ShadowColor
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource

fun Modifier.surface(
    shape: Shape = RectangleShape,
    background: BackgroundSpec? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 0.dp,
    elevation: Dp = 0.dp,
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = ShadowColor,
        spotColor = ShadowColor,
    )
    .foBackground(
        background = background,
        shape = shape,
    )
    .border(
        width = borderWidth,
        color = borderColor ?: Transparent,
        shape = shape,
    )
    .clip(shape)

fun Modifier.surface(
    shape: Shape = RectangleShape,
    background: Color? = null,
    borderColor: Color? = null,
    borderWidth: Dp = 0.dp,
    elevation: Dp = 0.dp,
): Modifier = this
    .shadow(
        elevation = elevation,
        shape = shape,
        ambientColor = ShadowColor,
        spotColor = ShadowColor,
    )
    .background(
        color = background ?: Transparent,
        shape = shape,
    )
    .border(
        width = borderWidth,
        color = borderColor ?: Transparent,
        shape = shape,
    )
    .clip(shape)

@Composable
fun Modifier.thClickable(
    onClick: (() -> Unit)?,
    enabled: Boolean = true,
    role: Role? = null,
    interactionSource: MutableInteractionSource? = remember { MutableInteractionSource() },
    indication: Indication? = ripple(color = THTheme.colors.content),
): Modifier =
    this.then(
        if (onClick == null) {
            Modifier
        } else {
            Modifier.composed {
                clickable(
                    enabled = enabled,
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = indication,
                    role = role,
                )
            }
        },
    )

@Composable
fun Modifier.thCard(
    backgroundColor: Color = THTheme.colors.surface1,
): Modifier =
    this.surface(
        shape = THTheme.shape.roundLarge,
        elevation = THTheme.elevation.medium,
        background = backgroundColor,
    )

fun Modifier.positionAwareImePadding() = composed {
    val density = LocalDensity.current
    var consumePadding by remember { mutableIntStateOf(0) }
    this@positionAwareImePadding
        .onGloballyPositioned { coordinates ->
            val rootCoordinate = coordinates.findRootCoordinates()
            val bottom = coordinates.positionInWindow().y + coordinates.size.height

            consumePadding = (rootCoordinate.size.height - bottom).toInt()
        }
        .consumeWindowInsets(PaddingValues(bottom = with(density) { consumePadding.toDp() }))
        .imePadding()
}

fun Modifier.foHazeChild(
    state: HazeState?,
    backgroundColor: Color,
): Modifier = this.then(
    if (state != null) {
        Modifier.hazeEffect(
            state = state,
            style = HazeStyle(
                backgroundColor = backgroundColor,
                tint = HazeTint(backgroundColor.copy(alpha = 0.5f)),
                blurRadius = BlurRadius,
                noiseFactor = 0.1f,
            )
        )
    } else {
        Modifier.background(backgroundColor)
    }
)

fun Modifier.safeHazeSource(
    state: HazeState?,
): Modifier = this.then(
    if (state != null) {
        Modifier.hazeSource(state)
    } else {
        Modifier
    }
)

fun Modifier.drawLineTop(
    stroke: Dp,
    color: Color,
): Modifier = composed {
    val density = LocalDensity.current
    val strokeWidthPx = density.run { stroke.toPx() }

    this.drawBehind {
        drawLine(
            color = color,
            start = Offset.Zero,
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidthPx,
        )
    }
}

private
val BlurRadius: Dp = 8.dp
