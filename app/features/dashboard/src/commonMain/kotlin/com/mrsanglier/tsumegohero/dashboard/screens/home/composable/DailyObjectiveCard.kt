package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_forward
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_brain
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_done_star_filled
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_flash
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_puzzle
import com.mrsanglier.tsumegohero.coreui.componants.icon.Content
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSize
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.spacer.THHorizontalSpacer
import com.mrsanglier.tsumegohero.coreui.componants.text.THText
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.extension.thCard
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

private val ProgressHeight: Dp = 3.dp

@Composable
fun DailyObjectiveCard(
    title: TextSpec,
    icon: IconSpec,
    trailingIcon: IconSpec,
    alpha: Float,
    attempts: ComposeProvider<List<Color>>,
    doneText: TextSpec,
    totalText: TextSpec,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .thCard()
            .thClickable(onClick)
            .padding(THTheme.spacing.large)
            .alpha(alpha),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon.Content(iconSize = IconSize.Regular)
        THTheme.spacing.small.THHorizontalSpacer()


        MainContent(
            title = title,
            doneText = doneText,
            totalText = totalText,
            attempts = attempts,
            modifier = Modifier.weight(1f),
        )
        THTheme.spacing.large.THHorizontalSpacer()

        trailingIcon.Content(iconSize = IconSize.Small)
    }

}

@Composable
private fun RowScope.MainContent(
    title: TextSpec,
    doneText: TextSpec,
    totalText: TextSpec,
    attempts: ComposeProvider<List<Color>>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(THTheme.spacing.tiny),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            THText(
                text = title,
                style = THTheme.typography.title100,
                modifier = Modifier.weight(1f),
            )
            THText(
                text = doneText,
                style = THTheme.typography.content100Semibold,
            )
            THText(
                text = totalText,
                style = THTheme.typography.content50Semibold,
                color = THTheme.colors.contentSecondary,
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(THTheme.shape.circle),
            horizontalArrangement = Arrangement.spacedBy(THTheme.spacing.xtiny),
        ) {
            attempts().forEach { color ->
                Box(
                    modifier = Modifier
                        .height(ProgressHeight)
                        .weight(1f)
                        .background(color),
                )
            }
        }

    }
}

@Immutable
data class DailyObjectiveCardState(
    val attempts: List<Attempt.Result?>,
    val trainingMode: TrainingMode,
    val onClick: (() -> Unit),
) {
    @Composable
    fun Content(
        modifier: Modifier = Modifier,
    ) {
        val isComplete = remember(attempts) {
            attempts.none { it == null }
        }

        DailyObjectiveCard(
            title = when (trainingMode) {
                TrainingMode.Flash -> "Éclair".toTextSpec() // TODO: loco
                TrainingMode.Classical -> "Classique".toTextSpec() // TODO: loco
                TrainingMode.Difficult -> "Difficile".toTextSpec() // TODO: loco
            },
            icon = when (trainingMode) {
                TrainingMode.Flash -> THDrawable.ic_flash.toIconSpec { THTheme.colors.contentTint }
                TrainingMode.Classical -> THDrawable.ic_puzzle.toIconSpec { THTheme.colors.detailGreen }
                TrainingMode.Difficult -> THDrawable.ic_brain.toIconSpec { THTheme.colors.detailBlue }
            },
            trailingIcon = if (isComplete) {
                THDrawable.ic_done_star_filled.toIconSpec { THTheme.colors.contentTint }
            } else {
                THDrawable.ic_arrow_forward.toIconSpec { THTheme.colors.content }
            },
            alpha = if (isComplete) 0.5f else 1f,
            attempts = THTheme.composed {
                attempts.map { result ->
                    when (result) {
                        Attempt.Result.Success -> colors.detailGreen
                        Attempt.Result.Failure -> colors.contentCritical
                        Attempt.Result.Skip -> colors.contentSecondary
                        null -> colors.contentDisable
                    }
                }
            },
            doneText = attempts.count { it != null }.toString().toTextSpec(),
            totalText = "/${attempts.count()}".toTextSpec(),
            modifier = modifier,
            onClick = onClick.takeIf { !isComplete },
        )
    }
}
