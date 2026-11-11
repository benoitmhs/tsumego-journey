package com.mrsanglier.tsumegohero.coreui.componants.fabmenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_close
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.surface
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.loading.BackHandler
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.theme.THTheme

/**
 * Material 3 FAB menu: a toggle button that reveals [items] above it.
 * Without [label], the [icon] (usually "+") rotates by 45° to become a close cross while the menu is open.
 * With a [label], the button is extended (icon + text) and shrinks to a close button while the menu is open.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun THFabMenu(
    icon: IconSpec,
    items: List<THFabMenuItem>,
    modifier: Modifier = Modifier,
    label: TextSpec? = null,
    visible: Boolean = true,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }

    // A hidden menu does not stay open
    LaunchedEffect(visible) { if (!visible) expanded = false }

    BackHandler(enabled = expanded) { expanded = false }

    val closedContainerColor = THTheme.colors.surfaceAccent
    val openContainerColor = THTheme.colors.surface3
    val closedContentColor = THTheme.colors.contentOnSurfaceTint
    val openContentColor = THTheme.colors.content

    // Slides out below the screen edge (twice its height to also clear the bottom padding)
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = slideInVertically { it * 2 } + fadeIn(),
        exit = slideOutVertically { it * 2 } + fadeOut(),
    ) {
        FloatingActionButtonMenu(
            expanded = expanded,
            button = {
                if (label != null) {
                    ExtendedToggleButton(
                        expanded = expanded,
                        onExpandedChange = { expanded = it },
                        icon = icon,
                        label = label,
                        closedContainerColor = closedContainerColor,
                        openContainerColor = openContainerColor,
                        closedContentColor = closedContentColor,
                        openContentColor = openContentColor,
                    )
                } else {
                    ToggleFloatingActionButton(
                        checked = expanded,
                        onCheckedChange = { expanded = it },
                        containerColor = { progress -> lerp(closedContainerColor, openContainerColor, progress) },
                    ) {
                        icon.Content(
                            iconSize = IconSize.Regular,
                            tint = lerp(closedContentColor, openContentColor, checkedProgress),
                            modifier = Modifier.graphicsLayer { rotationZ = checkedProgress * OpenRotation },
                        )
                    }
                }
            },
        ) {
            items.forEach { item ->
                FloatingActionButtonMenuItem(
                    onClick = {
                        expanded = false
                        item.onClick()
                    },
                    text = {
                        THText(
                            text = item.label,
                            style = THTheme.typography.label100,
                            color = closedContentColor,
                        )
                    },
                    icon = {
                        item.icon.Content(
                            iconSize = IconSize.Regular,
                            tint = closedContentColor,
                        )
                    },
                    containerColor = closedContainerColor,
                )
            }
        }
    }
}

@Suppress("LongParameterList")
@Composable
private fun ExtendedToggleButton(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    icon: IconSpec,
    label: TextSpec,
    closedContainerColor: Color,
    openContainerColor: Color,
    closedContentColor: Color,
    openContentColor: Color,
) {
    val progress by animateFloatAsState(targetValue = if (expanded) 1f else 0f)
    val contentColor = lerp(closedContentColor, openContentColor, progress)

    Row(
        modifier = Modifier
            .surface(
                shape = THTheme.shape.roundLarge,
                background = lerp(closedContainerColor, openContainerColor, progress),
                elevation = THTheme.elevation.medium,
            )
            .toggleable(value = expanded, onValueChange = onExpandedChange, role = Role.Button)
            .animateContentSize()
            .heightIn(min = FabSize)
            .widthIn(min = FabSize)
            .padding(horizontal = THTheme.spacing.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        // The icon fades into a close cross while the menu opens
        Box(contentAlignment = Alignment.Center) {
            icon.Content(
                iconSize = IconSize.Regular,
                tint = contentColor,
                modifier = Modifier.graphicsLayer {
                    alpha = 1f - progress
                    rotationZ = progress * OpenRotation
                },
            )
            THDrawable.ic_close.toIconSpec().Content(
                iconSize = IconSize.Regular,
                tint = contentColor,
                modifier = Modifier.graphicsLayer {
                    alpha = progress
                    rotationZ = (progress - 1f) * OpenRotation
                },
            )
        }
        if (!expanded) {
            THText(
                text = label,
                style = THTheme.typography.label100Medium,
                color = contentColor,
                maxLines = 1,
                modifier = Modifier.padding(start = THTheme.spacing.medium),
            )
        }
    }
}

private const val OpenRotation: Float = 45f
private val FabSize: Dp = 56.dp
