package com.mrsanglier.tsumegohero.dashboard.screens.home.composable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_arrow_forward
import com.mrsanglier.tsumegohero.app.coreui.resources.ic_done_star_filled
import com.mrsanglier.tsumegohero.coreui.componants.cellobjective.CellObjective
import com.mrsanglier.tsumegohero.coreui.componants.icon.IconSpec
import com.mrsanglier.tsumegohero.coreui.componants.text.TextSpec
import com.mrsanglier.tsumegohero.coreui.extension.composed
import com.mrsanglier.tsumegohero.coreui.extension.model.icon
import com.mrsanglier.tsumegohero.coreui.extension.model.label
import com.mrsanglier.tsumegohero.coreui.extension.thCard
import com.mrsanglier.tsumegohero.coreui.extension.thClickable
import com.mrsanglier.tsumegohero.coreui.extension.toIconSpec
import com.mrsanglier.tsumegohero.coreui.extension.toTextSpec
import com.mrsanglier.tsumegohero.coreui.resources.THDrawable
import com.mrsanglier.tsumegohero.coreui.theme.THTheme
import com.mrsanglier.tsumegohero.coreui.utils.ComposeProvider
import com.mrsanglier.tsumegohero.data.model.game.Attempt
import com.mrsanglier.tsumegohero.data.model.game.TrainingMode

@Composable
fun DailyObjectiveCard(
    title: TextSpec,
    icon: IconSpec,
    trailingIcon: IconSpec?,
    alpha: Float,
    attempts: ComposeProvider<List<Color>>,
    doneText: TextSpec,
    totalText: TextSpec,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)?,
) {
    CellObjective(
        title = title,
        icon = icon,
        trailingIcon = trailingIcon,
        attempts = attempts,
        doneText = doneText,
        totalText = totalText,
        modifier = modifier
            .fillMaxWidth()
            .thCard()
            .thClickable(onClick)
            .padding(THTheme.spacing.large)
            .alpha(alpha),
    )
}

@Immutable
data class DailyObjectiveCardState(
    val attempts: List<Attempt.Result?>,
    val trainingMode: TrainingMode,
    val onClick: (() -> Unit)?,
) {
    @Composable
    fun Content(
        modifier: Modifier = Modifier,
    ) {
        val isComplete = remember(attempts) {
            attempts.none { it == null }
        }

        DailyObjectiveCard(
            title = trainingMode.label(),
            icon = trainingMode.icon(),
            trailingIcon = when {
                isComplete -> THDrawable.ic_done_star_filled.toIconSpec { THTheme.colors.contentTint }
                onClick != null -> THDrawable.ic_arrow_forward.toIconSpec { THTheme.colors.content }
                else -> null
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
