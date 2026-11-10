package com.mrsanglier.tsumegohero.problemedition.composable

import androidx.compose.foundation.layout.Arrangement
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_check
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_close
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_ink_eraser
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_next
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_previous
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_black
import com.mrsanglier.tsumegohero.app.coreui.resources.stone_white
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_tool_black
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_tool_erase
import com.mrsanglier.tsumegohero.app.coreui.resources.tsumegoEdition_tool_white
import com.mrsanglier.tsumegohero.problemedition.EditionTool
import com.mrsanglier.tsumegohero.problemedition.ProblemEditionViewModelState
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStatus
import com.mrsanglier.tsumegohero.coreui.componants.button.ButtonStyle
import com.mrsanglier.tsumegohero.coreui.componants.button.THButton
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.selector.THSegment
import com.mrsanglier.tsumegohero.coreui.componants.selector.THSegmentedSelector
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.resources.THString
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.theme.color.DarkGreen
import com.mrsanglier.tsumegohero.game.model.Stone
import org.jetbrains.compose.resources.DrawableResource

/** Stone images keep their own colors instead of being tinted like icons. */
private fun DrawableResource.toUntintedIconSpec(): IconSpec = toIconSpec(tint = { Color.Unspecified })

@Composable
internal fun StoneToolSelector(
    selectedTool: EditionTool,
    onSelectTool: (EditionTool) -> Unit,
    modifier: Modifier = Modifier,
) {
    THSegmentedSelector(
        segments = EditionTool.entries.map { tool ->
            when (tool) {
                EditionTool.Black -> THSegment(
                    label = THString.tsumegoEdition_tool_black.toTextSpec(),
                    icon = THDrawable.stone_black.toUntintedIconSpec(),
                )
                EditionTool.White -> THSegment(
                    label = THString.tsumegoEdition_tool_white.toTextSpec(),
                    icon = THDrawable.stone_white.toUntintedIconSpec(),
                )
                EditionTool.Erase -> THSegment(
                    label = THString.tsumegoEdition_tool_erase.toTextSpec(),
                    icon = THDrawable.ic_ink_eraser.toIconSpec(),
                )
            }
        },
        selectedIndex = selectedTool.ordinal,
        onSelect = { index -> onSelectTool(EditionTool.entries[index]) },
        modifier = modifier,
        selectedBackground = THTheme.colors.surface3,
    )
}

/** Black / White choice: compact segments sized to their text, bigger label than the tool selector. */
@Composable
internal fun FirstPlayerSelector(
    firstPlayer: Stone,
    onSelectFirstPlayer: (Stone) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clip(THTheme.shape.roundMedium),
        horizontalArrangement = Arrangement.spacedBy(FirstPlayerSegmentSpacing),
    ) {
        Stone.entries.forEach { stone ->
            val style = if (stone == firstPlayer) ButtonStyle.Primary else ButtonStyle.Text
            val label = when (stone) {
                Stone.BLACK -> THString.tsumegoEdition_tool_black
                Stone.WHITE -> THString.tsumegoEdition_tool_white
            }
            Box(
                modifier = Modifier
                    .heightIn(min = FirstPlayerSegmentMinHeight)
                    .surface(background = style.background(ButtonStatus.Enabled))
                    .thClickable(onClick = { onSelectFirstPlayer(stone) }, role = Role.Tab)
                    .padding(horizontal = THTheme.spacing.xlarge, vertical = THTheme.spacing.small),
                contentAlignment = Alignment.Center,
            ) {
                THText(
                    text = label.toTextSpec(),
                    style = THTheme.typography.label200,
                    color = style.contentColor(ButtonStatus.Enabled),
                    maxLines = 1,
                )
            }
        }
    }
}

/** Section title inside a step. */
@Composable
internal fun EditionSectionHeader(
    text: TextSpec,
    modifier: Modifier = Modifier,
) {
    THText(
        text = text,
        style = THTheme.typography.title100,
        modifier = modifier,
    )
}

/** Review-like controls: previous / next move, mark the current leaf as correct or wrong, delete the move. */
@Composable
internal fun SolutionsStepActions(
    uiState: ProblemEditionViewModelState,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleOutcome: (isCorrect: Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.small, Alignment.CenterHorizontally),
    ) {
        ActionIconButton(
            icon = THDrawable.ic_previous.toIconSpec(),
            enabled = uiState.canGoPrevious,
            onClick = onPrevious,
        )
        ActionIconButton(
            icon = THDrawable.ic_next.toIconSpec(),
            enabled = uiState.canGoNext,
            onClick = onNext,
        )
        ActionIconButton(
            icon = THDrawable.ic_check.toIconSpec(tint = if (uiState.canMark) ({ DarkGreen }) else null),
            enabled = uiState.canMark,
            isActive = uiState.currentOutcome == true,
            onClick = { onToggleOutcome(true) },
        )
        ActionIconButton(
            icon = THDrawable.ic_close.toIconSpec(tint = if (uiState.canMark) ({ Color.Red }) else null),
            enabled = uiState.canMark,
            isActive = uiState.currentOutcome == false,
            onClick = { onToggleOutcome(false) },
        )
        ActionIconButton(
            icon = THDrawable.ic_ink_eraser.toIconSpec(),
            enabled = uiState.canDelete,
            onClick = onDelete,
        )
    }
}

@Composable
private fun ActionIconButton(
    icon: IconSpec,
    enabled: Boolean,
    onClick: () -> Unit,
    isActive: Boolean = false,
) {
    THButton(
        text = null,
        icon = icon,
        onClick = onClick,
        style = if (isActive) ButtonStyle.Primary else ButtonStyle.Text,
        status = if (enabled) ButtonStatus.Enabled else ButtonStatus.Disabled,
    )
}

private val FirstPlayerSegmentSpacing: Dp = 2.dp
private val FirstPlayerSegmentMinHeight: Dp = 48.dp
